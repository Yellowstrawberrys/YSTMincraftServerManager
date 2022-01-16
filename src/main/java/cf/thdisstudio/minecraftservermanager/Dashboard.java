package cf.thdisstudio.minecraftservermanager;

import cf.thdisstudio.minecraftservermanager.Data.SaveAndLoad;
import cf.thdisstudio.minecraftservermanager.Data.ServerCreationData;
import cf.thdisstudio.minecraftservermanager.Data.ServerType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static cf.thdisstudio.minecraftservermanager.CurrentData.selectedServer;

public class Dashboard {

    String[] serverData;

    Process server;

    boolean isStoppingTheServer = false;

    @FXML
    public TextArea LLog;

    @FXML
    public TextField Send;

    @FXML
    public TabPane Tabs;

    @FXML
    public ListView PluginList;

    Thread logging;
    Thread ErrorLogging;

    @FXML
    public void OnSend(){
        try {
            if(server != null) {
                server.getOutputStream().write((Send.getText()+"\n").getBytes(StandardCharsets.UTF_8));
                server.getOutputStream().flush();
            }
            Send.setText("");
        } catch (IOException e) {
            YSTApplication.error(e);
        }
    }

    @FXML
    public void initialize(){
        try {
            serverData = SaveAndLoad.loadServer(selectedServer);
            ServerCreationData.UsingRam = Double.parseDouble(serverData[4]);
            if(serverData[3].equals("Vanilla")){
                Tabs.getTabs().remove(3,5);
            }else{
                if(!new File(selectedServer.getAbsolutePath() + "\\plugins").exists())
                    new File(selectedServer.getAbsolutePath() + "\\plugins").mkdirs();
                for(File plugin : Objects.requireNonNull(new File(selectedServer.getAbsolutePath() + "\\plugins").listFiles(File::isFile))){
                    PluginList.getItems().add(plugin.getName());
                }
                PluginList.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".jar");
                    if(db.hasFiles() && isAccepted){
                        for(File plugin : db.getFiles()){
                            File copied = new File(selectedServer.getAbsolutePath()+"\\plugins\\"+plugin.getName());
                            try (
                                    InputStream in = new BufferedInputStream(
                                            new FileInputStream(plugin));
                                    OutputStream out = new BufferedOutputStream(
                                            new FileOutputStream(copied))) {

                                byte[] buffer = new byte[1024];
                                int lengthRead;
                                while ((lengthRead = in.read(buffer)) > 0) {
                                    out.write(buffer, 0, lengthRead);
                                    out.flush();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else
                        event.consume();
                });
            }
            LLog.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {LLog.setScrollTop(0);LLog.setScrollLeft(0);});
        } catch (IOException e) {
            YSTApplication.error(e);
        }
    }

    @FXML
    public void Start(){
        if(server == null || !server.isAlive())
            startServer();
    }

    @FXML
    public void Restart(){
        if(server != null && server.isAlive()){
            stopServer();
            startServer();
        }
    }

    @FXML
    public void Stop(){
        if(server.isAlive())
            stopServer();
    }

    public void stopServer(){
        Platform.runLater(() -> {
            isStoppingTheServer = true;
            LLog.appendText("\n서버를 종료중... | YST Minecraft Server Manager");
            try {
                server.getOutputStream().write("stop\n".getBytes(StandardCharsets.UTF_8));
                server.getOutputStream().flush();
            } catch (IOException e) {
                YSTApplication.error(e);
            }
            new Thread(() -> {
                boolean isNotStopped = true;
                while (isNotStopped){
                    if(!server.isAlive()) {
                        LLog.appendText("\n서버를 성공적으로 종료하였습니다!");
                        logging.stop();
                        ErrorLogging.stop();
                        isNotStopped = false;
                    }
                }
            }).start();
        });
    }

    private void startServer(){
        LLog.clear();
        LLog.appendText("서버를 시작중... | YST Minecraft Server Manager");
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C", "java",
                "-server", "-Xms512M", "-Xmx"+((int) (ServerCreationData.UsingRam*1024))+"M"
                , "-jar", "server.jar", "nogui");
        processBuilder.directory(selectedServer);
        try {
            server = processBuilder.start();
            CurrentData.CurrentServer = server;
            System.out.println(server.isAlive());

            //서버 재시작시 java.lang.reflect.InvocationTargetException 문제 해결 방안
            logging = new Thread(() -> {
                try {
                    BufferedReader input = new BufferedReader(new
                            InputStreamReader(server.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        String finalLine = line;
                        if(line.contains("Stopping the server") && !isStoppingTheServer)
                            stopServer();
                        Platform.runLater(() -> LLog.appendText("\n"+finalLine));
                        System.out.println(line);
                    }
                    input.close();
                    LLogChecker();
                }catch (Exception e){
                    YSTApplication.error(e);
                }
            });
            ErrorLogging = new Thread(() -> {
                try {
                    BufferedReader input = new BufferedReader(new
                            InputStreamReader(server.getErrorStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> LLog.appendText("\n"+finalLine));
                        System.out.println(line);
                    }
                    input.close();
                    LLogChecker();
                }catch (Exception e){
                    YSTApplication.error(e);
                }
            });
            logging.start();
            ErrorLogging.start();
        } catch (IOException e) {
            YSTApplication.error(e);
        }
    }

    public void LLogChecker(){
        if(LLog.getText().length() > 6000){
            Platform.runLater(() -> LLog.setText(LLog.getText(LLog.getLength()-6000, LLog.getLength())));
        }
    }
}
