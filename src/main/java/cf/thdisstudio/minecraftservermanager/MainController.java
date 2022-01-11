package cf.thdisstudio.minecraftservermanager;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML
    private WebView Web;

    String ServerDiv =
            "<div id=\"%s\">\n" +
            "    <img src=\""+getClass().getResource("yst.png").getFile().replaceAll("%", "%%")+"\" width=\"120px\" align=\"left\">\n" +
            "    <br/>\n" +
            "    <p style=\"\">%s - %s</p>\n" +
            "    <p style=\"color: grey; font-size: 13px\">%s</p>\n" +
            "    <form action=\"%s\"><input type=\"submit\" style=\"right:1px; background-color: white; border: black solid 1px;\" value=\"설정하러 가기\" /></form>\n" +
            "</div><hr/>";

    @FXML
    public void initialize(){
        new Thread(() -> {
            File file = new File(System.getenv("APPDATA")+"\\YST Minecraft Server Manager\\Servers\\");
            if(!file.exists())
                file.mkdirs();

            StringBuilder stb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<html>" +
                    "<body>");
            int i = 0;
            for(File versionFolder : file.listFiles(File::isDirectory)){
                for(File ServerFolder : versionFolder.listFiles(File::isDirectory)){
                    i++;
                    stb.append(String.format(ServerDiv, versionFolder.getName()+"."+ServerFolder.getName(), ServerFolder.getName(), versionFolder.getName(), "Null",
                            "https://"+versionFolder.getName()+"NAME"+ServerFolder.getName()+".YSTSERVERMANAGER"));
                }
            }
            stb.append("</body>" +
                    "</html>");
            Platform.runLater(() -> Web.getEngine().loadContent(stb.toString()));
        }).start();
        Web.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.RUNNING.equals(newValue)) {
                String location = Web.getEngine().getLocation();
                if(location.contains(".ystservermanager")){
                    location = location.replaceFirst("https://", "");
                    try {
                        for(File server : new File(System.getenv("APPDATA")+"\\YST Minecraft Server Manager\\Servers\\"+location.split("name")[0]+"\\")
                                .listFiles(File::isDirectory)){
                            System.out.println(server.getAbsolutePath()+" / "+location.split("name")[1].replaceAll("\\.ystservermanager/\\?", ""));
                            if(server.getName().toLowerCase().equals(location.split("name")[1].replaceAll("\\.ystservermanager/\\?", ""))){
                                CurrentData.selectedServer = server;
                                YSTApplication.sta.setScene(new Scene(new FXMLLoader(getClass().getResource("Dashboard.fxml")).load(), 752, 440));
                                System.out.println(server);
                            }
                        }
                    } catch (IOException e) {
                        YSTApplication.error(e);
                    }
                }
            }
        });
    }

    @FXML
    public void NewServer() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YSTApplication.class.getResource("Create.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 752, 440);
        YSTApplication.sta.setScene(scene);
    }
}