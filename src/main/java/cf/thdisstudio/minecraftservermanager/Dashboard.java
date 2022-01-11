package cf.thdisstudio.minecraftservermanager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.*;

import static cf.thdisstudio.minecraftservermanager.CurrentData.selectedServer;

public class Dashboard {

    Process server;

    @FXML
    public TextArea LLog;

    @FXML
    public TextField Send;

    Thread logging = new Thread(() -> {
        try {
            BufferedReader input = new BufferedReader(new
                    InputStreamReader(server.getInputStream()));
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
    Thread ErrorLogging = new Thread(() -> {
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

    Writer writer;

    @FXML
    public void OnSend(){
        try {
            if(server != null) {
                writer.write(Send.getText());
            }
            Send.setText("");
        } catch (IOException e) {
            YSTApplication.error(e);
        }
    }

    @FXML
    public void initialize(){
        ServerCreationData.UsingRam = 4;
        LLog.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {LLog.setScrollTop(0);LLog.setScrollLeft(0);});
    }

    @FXML
    public void Start(){
        if(server == null || !server.isAlive())
            startServer();
    }

    @FXML
    public void Restart(){
        if(server != null && server.isAlive()){
            try {
                writer.write(Send.getText());
            } catch (IOException e) {
                YSTApplication.error(e);
            }
            startServer();
        }
    }

    @FXML
    public void Stop(){
        try {
            writer.write(Send.getText());
        } catch (IOException e) {
            YSTApplication.error(e);
        }
    }

    private void startServer(){
        LLog.appendText("서버를 시작중... | YST Minecraft Server Manager");
        ProcessBuilder processBuilder = new ProcessBuilder("java",
                "-server", "-Xms512M", "-Xmx"+((int) (ServerCreationData.UsingRam*1024))+"M"
                , "-jar", "server.jar", "nogui");
        processBuilder.directory(selectedServer);
        try {
            server = processBuilder.start();
            System.out.println(server.isAlive());
            logging.start();
            ErrorLogging.start();
            writer = new OutputStreamWriter(server.getOutputStream());
        } catch (IOException e) {
            YSTApplication.error(e);
        }
    }
}
