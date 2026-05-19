package com.pazos.wtovrmanager.controller;

import com.pazos.wtovrmanager.component.RingPanel;
import com.pazos.wtovrmanager.model.backendModels.Match;
import com.pazos.wtovrmanager.service.ApiService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MainController {

    @FXML private HBox ringsContainer;

    private final ApiService api = new ApiService();
    private String wsUrl;

    @FXML
    public void initialize() {
        Properties config = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            config.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        wsUrl = config.getProperty("api.websocket.url", "");
        loadRings();
    }

    @FXML
    private void loadRings() {
        ringsContainer.getChildren().clear();

        Task<List<Match>> task = new Task<>() {
            @Override
            protected List<Match> call() throws Exception {
                return api.getMatches();
            }
        };

        task.setOnSucceeded(e -> {
            List<Match> matches = task.getValue();
            new TreeSet<>(
                matches.stream()
                    .map(m -> m.getMat() != null ? m.getMat() : 0)
                    .collect(Collectors.toList())
            ).forEach(ring -> {
                RingPanel panel = new RingPanel(wsUrl, ring, api);
                HBox.setHgrow(panel, Priority.ALWAYS);
                ringsContainer.getChildren().add(panel);
            });
        });

        task.setOnFailed(e -> {
            Label err = new Label("Error: " + task.getException().getMessage());
            err.getStyleClass().add("label-subtitle");
            ringsContainer.getChildren().add(err);
        });

        new Thread(task).start();
    }
}
