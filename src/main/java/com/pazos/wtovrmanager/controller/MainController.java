package com.pazos.wtovrmanager.controller;

import com.pazos.wtovrmanager.I18n;
import com.pazos.wtovrmanager.component.RingPanel;
import com.pazos.wtovrmanager.component.SelectableCard;
import com.pazos.wtovrmanager.model.backendModels.Athlete;
import com.pazos.wtovrmanager.model.backendModels.Category;
import com.pazos.wtovrmanager.model.backendModels.Match;
import com.pazos.wtovrmanager.service.ApiService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MainController {

    @FXML private HBox ringsContainer;
    @FXML private ScrollPane ringsScroll;
    @FXML private ScrollPane generatorScroll;
    @FXML private FlowPane generatorContainer;

    private final ApiService api = new ApiService();
    private String wsUrl;
    private Category categoryNewMatch;
    private List<Athlete> athletesNewMatch;
    private Match newMatch;

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
                ringsContainer.getChildren().add(new RingPanel(wsUrl, ring, api));
            });
        });

        task.setOnFailed(e -> {
            Label err = new Label(I18n.get("error.loading", task.getException().getMessage()));
            err.getStyleClass().add("label-subtitle");
            ringsContainer.getChildren().add(err);
        });

        new Thread(task).start();
    }
    @FXML
    private void loadRingsView() {
        showView(true);
        loadRings();
    }

    @FXML
    private void newMatchGenerator() {
        showView(false);
        generatorContainer.getChildren().clear();
        try {
            List<Category> categories = api.getCategories();
            categories.forEach(c -> generatorContainer.getChildren().add(
                new SelectableCard<>(c, selected -> this.categoryNewMatch = selected)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showView(boolean rings) {
        ringsScroll.setVisible(rings);
        ringsScroll.setManaged(rings);
        generatorScroll.setVisible(!rings);
        generatorScroll.setManaged(!rings);
    }
}
