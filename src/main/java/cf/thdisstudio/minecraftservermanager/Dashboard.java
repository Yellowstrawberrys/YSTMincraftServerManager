package cf.thdisstudio.minecraftservermanager;

import cf.thdisstudio.minecraftservermanager.Data.SaveAndLoad;
import cf.thdisstudio.minecraftservermanager.Data.ServerCreationData;
import cf.thdisstudio.minecraftservermanager.Data.ServerType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
}
