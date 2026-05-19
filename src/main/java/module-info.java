module net.gestores.wtovrmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires atlantafx.base;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens net.gestores.wtovrmanager to javafx.fxml;
    opens net.gestores.wtovrmanager.controller to javafx.fxml, javafx.graphics;
    opens net.gestores.wtovrmanager.model to com.fasterxml.jackson.databind;
    exports net.gestores.wtovrmanager;
    exports net.gestores.wtovrmanager.model;
}