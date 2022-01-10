package cf.thdisstudio.minecraftservermanager;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

public class Dashboard {

    @FXML
    public ScrollPane Logs;

    @FXML
    public TextField Send;

    @FXML
    public void OnSend(){

    }

    @FXML
    public void initialize(){
        new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
