package com.pazos.wtovrmanager.controller;

import animatefx.animation.AnimationFX;
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
import javafx.util.Duration;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MainController {

    @FXML private HBox ringsContainer;
    @FXML private ScrollPane ringsScroll;
    @FXML private ScrollPane generatorScroll;
    @FXML private FlowPane categoriesContainer;
    @FXML private FlowPane athletesContainer;

    private final ApiService api = new ApiService();
    private String wsUrl;
    private Category categoryNewMatch;
    private SelectableCard<Category> selectedCategoryCard;
    private SelectableCard<Athlete> blueCard;
    private SelectableCard<Athlete> redCard;
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
        categoriesContainer.getChildren().clear();
        athletesContainer.getChildren().clear();
        selectedCategoryCard = null;
        categoryNewMatch = null;
        blueCard = null;
        redCard  = null;
        loadSelectionCategory();
    }

    private void loadSelectionCategory() {
        try {
            List<Category> categories = api.getCategories();
            categories.forEach(c -> categoriesContainer.getChildren().add(
                    new SelectableCard<>(c, (categorySelected, card) -> {
                        if (categoryNewMatch != null && categoryNewMatch.getId() == categorySelected.getId()) {
                            return;
                        }
                        if (selectedCategoryCard != null) {
                            selectedCategoryCard.deselect();
                        }
                        categoryNewMatch = categorySelected;
                        selectedCategoryCard = card;
                        card.selectBlue();
                        athletesContainer.getChildren().clear();
                        blueCard = null;
                        redCard  = null;
                        try {
                            List<Athlete> athletes = api.getAthletesCategory(categoryNewMatch.getId());
                            loadSelectionAthletes(athletes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSelectionAthletes(List<Athlete> athletes) {
        athletes.forEach(a -> {
            SelectableCard<Athlete> cardNew = new SelectableCard<>(a, (athleteSelected, card) -> {
                if (card == blueCard) {
                    blueCard = null;
                    card.deselect();
                } else if (card == redCard) {
                    redCard = null;
                    card.deselect();
                } else if (blueCard == null) {
                    blueCard = card;
                    card.selectBlue();
                } else if (redCard == null) {
                    redCard = card;
                    card.selectRed();
                }
            });
            cardNew.setOpacity(0);
            athletesContainer.getChildren().add(cardNew);
            AnimationFX anim = new animatefx.animation.RotateInUpLeft(cardNew);
            anim.setSpeed(1.5);
            anim.setDelay(Duration.millis(100 * athletesContainer.getChildren().size()));
            anim.setOnFinished(event -> cardNew.setOpacity(1));
            anim.play();
        });
    }
    private void showView(boolean rings) {
        ringsScroll.setVisible(rings);
        ringsScroll.setManaged(rings);
        generatorScroll.setVisible(!rings);
        generatorScroll.setManaged(!rings);
    }
}
