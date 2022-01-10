module cf.thdisstudio.minecraftservermanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
//    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.json;
    requires jdk.management;
    requires org.jsoup;
    requires com.gluonhq.charm.glisten;

    opens cf.thdisstudio.minecraftservermanager to javafx.fxml;
    exports cf.thdisstudio.minecraftservermanager;
}