module com.pazos.wtovrmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires atlantafx.base;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires AnimateFX;

    opens com.pazos.wtovrmanager to javafx.fxml;
    opens com.pazos.wtovrmanager.controller to javafx.fxml, javafx.graphics;
    opens com.pazos.wtovrmanager.component to javafx.fxml;
    opens com.pazos.wtovrmanager.model to com.fasterxml.jackson.databind;
    exports com.pazos.wtovrmanager;
    exports com.pazos.wtovrmanager.model;
    exports com.pazos.wtovrmanager.model.backendModels;
    opens com.pazos.wtovrmanager.model.backendModels to com.fasterxml.jackson.databind;
}