package cf.thdisstudio.minecraftservermanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class YSTApplication extends Application {

    public static Stage sta;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YSTApplication.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 752, 440);
        stage.setResizable(false);
        stage.setTitle("노랑딸기 서버 구동기 | Ver. 1.0");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("yst.png"))));
        stage.setScene(scene);
        stage.show();
        sta = stage;
    }

    public static void error(Exception ex){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("프로그램 오류");
            alert.setHeaderText("프로그램 오류(개발자에게 오류를 제보해주세요!)");
            alert.setContentText("개발자에게 오류를 제보하기: https://github.com/Yellowstrawberrys/YSTMincraftServerManager/issues/new");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("오류 로그(제보하실때 해당부분을 꼭 복사해주세요!):");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(true);

            alert.showAndWait();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}