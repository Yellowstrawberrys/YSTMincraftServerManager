package cf.thdisstudio.minecraftservermanager;

import cf.thdisstudio.minecraftservermanager.Data.ServerCreationData;
import cf.thdisstudio.minecraftservermanager.Data.ServerType;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class Create {

    @FXML
    private TextField name;

    @FXML
    private TextField Description;

    @FXML
    private ComboBox<String> ServerEngine;

    @FXML
    private ComboBox<String> version;

    @FXML
    private ImageView ServerIcon;

    @FXML
    private Button CreateButton;

    @FXML
    public Slider RamScroll;

    @FXML
    private TextField RAMValue;

    public double Ram;

    public String ServerEngineName = "";
    public String ServerVersion = "";
    HashMap<String, String> downloadLinks = new HashMap<>();

    @FXML
    public void initialize() {
        ServerIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("")).getFile()));
        ServerEngine.getItems().add("Vanilla");
        ServerEngine.getItems().add("Bukkit");
        ServerEngine.getItems().add("Spigot");
//        ServerEngine.getItems().add("Paper");
//        ServerEngine.getItems().add("Forge");
//        ServerEngine.getItems().add("MohistMC");
//        ServerEngine.getItems().add("Fabric");
//        ServerEngine.getItems().add("Bedrock");
//        ServerEngine.getItems().add("PocketMine");
        ServerEngine.addEventHandler(EventType.ROOT, event -> {
            if(event.getEventType().getName().equals("COMBO_BOX_BASE_ON_HIDDEN")){
                version.getItems().clear();
                version.getItems().add("로딩중...");
                put(ServerEngine.getValue());
            }
        });
        version.addEventHandler(EventType.ROOT, event -> {
            if(event.getEventType().getName().equals("COMBO_BOX_BASE_ON_HIDDEN")){
                ServerVersion = version.getValue();
            }
        });

        ServerIcon.setImage(new Image(getClass().getResource("DragDrop.png").getFile()));

        ServerIcon.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png")
                    || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpeg")
                    || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");

            if(db.hasFiles() && isAccepted){
                ServerIcon.setImage(db.getImage());
            }else
                event.consume();
        });

        ServerIcon.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("서버 이미지 선택");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ALL Images", "*.jpeg, *.jpg, *.png"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            File selectedFile = fileChooser.showOpenDialog(YSTApplication.sta);
            ServerIcon.setImage(new Image("file:///"+selectedFile.getAbsolutePath()));
        });
    }

    private void put(String name){
        this.ServerEngineName = name;
        new Thread(() -> {
            downloadLinks.clear();
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonReader.ReadFromUrl("https://yellowstrawberrys.github.io/MinecraftServerVersionWebSite/Versions.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> version.getItems().clear());
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject current = jsonArray.getJSONObject(i);
                Platform.runLater(() -> version.getItems().add(current.getString("version")));
                downloadLinks.put(current.getString("version"), current.getString("downloadLink"));
            }
        }).start();
    }

    @FXML
    public void RamScrollChanged(){
        long ramAsByte = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean()).getTotalPhysicalMemorySize();

        Ram = (RamScroll.getValue() / 100d) * (ramAsByte / 1073741824d);

        if (Ram < 0.512)
            Ram = 0.512;

        RAMValue.setText(String.valueOf(Ram));
    }

    @FXML
    public void RamValueChanged(){
        try {
            Ram = Double.parseDouble(RAMValue.getText());

            if (Ram < 0.512)
                Ram = 0.512;

        }catch (Exception e){
            Ram = 0.512;
        }
        RamScroll.setValue((Ram / (((com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1073741824d)) * 100d);
    }

    @FXML
    public void OnCreate() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("마인크래프트 최종 사용자 라이선스 계약 동의");
            alert.setHeaderText("마인크래프트 EULA(최종 사용자 라이선스 계약)에 동의하십니까?");
            alert.setContentText("마인크래프트 EULA: https://account.mojang.com/documents/minecraft_eula");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                CreateButton.setCursor(Cursor.WAIT);
                CreateButton.setDisable(true);
                ServerCreationData.name = this.name.getText();
                ServerCreationData.Description = this.Description.getText();
                ServerCreationData.version = this.ServerVersion;
                ServerCreationData.ServerEngine = this.ServerEngineName;
                ServerCreationData.downloadingURL = downloadLinks.get(ServerVersion);
                ServerCreationData.UsingRam = this.Ram;
                ServerCreationData.icon = this.ServerIcon.getImage();
                ServerCreationData.serverType = ServerType.valueOf(ServerEngine.getValue());
                YSTApplication.sta.setScene(new Scene(new FXMLLoader(YSTApplication.class.getResource("Creating.fxml")).load(), 752, 440));
            }
        }catch (Exception e){
            YSTApplication.error(e);
        }
    }
}
