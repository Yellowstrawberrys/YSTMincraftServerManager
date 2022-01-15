package cf.thdisstudio.minecraftservermanager;

import cf.thdisstudio.minecraftservermanager.Data.SaveAndLoad;
import cf.thdisstudio.minecraftservermanager.Data.ServerCreationData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import com.gluonhq.charm.glisten.control.ProgressBar;
import com.gluonhq.charm.glisten.control.ProgressIndicator;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CreatingController {

    @FXML
    private Label Status;

    @FXML
    private ProgressIndicator Progress;

    @FXML
    private ProgressBar progressBarr;

    int byteDownloaded = 0;

    int byteDownloadedInSecond = 0;

    int timeLeftSecond = 0;

    @FXML
    public void initialize() {
        new Thread(() -> {
            Status.setText("서버 폴더를 생성중");
            String rootFolder = System.getenv("APPDATA")+"\\YST Minecraft Server Manager\\Servers\\"+ ServerCreationData.version+"\\";
            File folder = new File(rootFolder);
            if(!folder.exists())
                folder.mkdirs();

            File currentFolder = new File(rootFolder+ServerCreationData.name);
            if(currentFolder.exists()){
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("오류 - 해당 서버 이름이 이미 존재함");
                    alert.setHeaderText("오류");
                    alert.setContentText("이미 생성하려는 이름에 서버가 해당 버전에 존재 합니다!");

                    alert.showAndWait();
                    try {
                        YSTApplication.sta.setScene(new Scene(new FXMLLoader(YSTApplication.class.getResource("Main.fxml")).load(), 752, 440));
                    } catch (IOException e) {
                        YSTApplication.error(e);
                    }
                });
            }else{
                currentFolder.mkdirs();
                try(BufferedInputStream in = new BufferedInputStream(new URL(ServerCreationData.downloadingURL).openStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(currentFolder.getAbsolutePath()+"\\server.jar")){
                    //서버 파일 다운로드
                    URL url = new URL(ServerCreationData.downloadingURL);
                    URLConnection conn = url.openConnection();
                    int size = conn.getContentLength();
                    double currentSize = 0.0;

                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;

                    Thread th = new Thread(() -> {
                        while (true){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            timeLeftSecond = (size-byteDownloaded) / byteDownloadedInSecond;
                            byteDownloadedInSecond = 0;
                        }
                    });
                    th.start();
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        byteDownloaded += bytesRead;
                        byteDownloadedInSecond += bytesRead;

                        currentSize += bytesRead;
                        double finalCurrentSize = currentSize;
                        Platform.runLater(() -> {Status.setText((String.format("서버 파일을 다운로드 받는중... (%03.02f%% | 남은 시간: %s초)", finalCurrentSize/size*100f, timeLeftSecond)));Progress.setProgress(finalCurrentSize/size);progressBarr.setProgress(finalCurrentSize/size-0.12);});
                    }
                    th.stop();
                    //동의 파일 생성
                    Platform.runLater(() -> Status.setText("EULA 동의 파일 생성중..."));
                    File eula = new File(currentFolder.getAbsolutePath()+"\\eula.txt");
                    eula.createNewFile();
                    Writer writer = new FileWriter(currentFolder.getAbsolutePath()+"\\eula.txt");
                    writer.write("eula=true");
                    writer.flush();
                    writer.close();
                    Platform.runLater(() -> progressBarr.setProgress(0.9));
                    SaveAndLoad.newServer(ServerCreationData.name, ServerCreationData.Description, ServerCreationData.version, String.valueOf(ServerCreationData.serverType), ServerCreationData.UsingRam, currentFolder);
                    CurrentData.selectedServer = currentFolder;
                    Platform.runLater(() -> {Status.setText("🎉 생성 완료! (5초 후 관리창으로 넘어갑니다.)");progressBarr.setProgress(1);});
                    Thread.sleep(5000);
                    Platform.runLater(() -> {
                        try {
                            YSTApplication.sta.setScene(new Scene(new FXMLLoader(getClass().getResource("Dashboard.fxml")).load(), 752, 440));
                        } catch (IOException e) {
                            YSTApplication.error(e);
                        }
                    });
                    //서버 첫 시작
//                    Platform.runLater(() -> progressBarr.setProgress(0.91));
//                    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "java",
//                            "-server", "-Xms512M", "-Xmx"+((int) (ServerCreationData.UsingRam*1024))+"M"
//                    , "-jar", "server.jar", "nogui");
//                    processBuilder.directory(currentFolder);
//                    Platform.runLater(() -> progressBarr.setProgress(0.93));
////                    processBuilder.command("java -server -XX:+AggressiveOpts -XX:+UseConcMarkSweepGC " +
////                            "-XX:+UnlockExperimentalVMOptions -XX:+UseParNewGC -XX:+ExplicitGCInvokesConcurrent " +
////                            "-XX:+UseFastAccessorMethods -XX:+UseConcMarkSweepGC -XX:+UseParNewGC " +
////                            "-XX:+CMSIncrementalPacing -XX:+AggressiveOpts -Xms512mb -Xmx"+((int) (ServerCreationData.UsingRam*1024))+"b -jar server.jar");
//                    Process process = processBuilder.start();
//                    Platform.runLater(() -> {progressBarr.setProgress(0.95);Status.setText("서버를 시작하는 중...");Progress.setProgress(-0.0930);});
//                    BufferedReader input = new BufferedReader(new
//                            InputStreamReader(process.getInputStream()));
//                    System.out.println(process.isAlive());
//                    String line;
//                    while ((line = input.readLine()) != null) {
//                        if(line.contains("Done (") && line.contains("For help, type \"help\"")){
//                            Platform.runLater(() -> {Status.setText("🎉 생성 완료! (5초 후 관리창으로 넘어갑니다.)");progressBarr.setProgress(1);});
//                            process.getOutputStream().write("stop".getBytes(StandardCharsets.UTF_8));
//                        }
//                        System.out.println(line);
//                    }
//                    input.close();
                }catch (MalformedURLException e) {
                    YSTApplication.error(e);
                } catch (IOException e) {
                    YSTApplication.error(e);
                } catch (InterruptedException e) {
                    YSTApplication.error(e);
                }
            }
        }).start();
    }

}
