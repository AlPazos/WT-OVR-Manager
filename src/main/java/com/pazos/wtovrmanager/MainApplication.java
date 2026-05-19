package com.pazos.wtovrmanager;

import atlantafx.base.theme.PrimerDark;
import com.pazos.wtovrmanager.component.MatchCard;
import javafx.application.Application;
import javafx.css.Match;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Properties config = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            config.load(is);
        }

        Locale locale = Locale.forLanguageTag(config.getProperty("app.language", "en"));
        ResourceBundle bundle = ResourceBundle.getBundle("com.pazos.wtovrmanager.i18n.messages", locale);

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/pazos/wtovrmanager/view/main-view.fxml"),
                bundle
        );


        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        java.net.URL css = getClass().getResource(config.getProperty("app.css", ""));
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        primaryStage.setTitle(bundle.getString("app.title"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
