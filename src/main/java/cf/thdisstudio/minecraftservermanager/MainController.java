package cf.thdisstudio.minecraftservermanager;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML
    private WebView Web;

    @FXML
    public void initialize(){
        Web.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.RUNNING.equals(newValue)) {
                System.out.println(Web.getEngine().getLocation());
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