package com.pazos.wtovrmanager.component;

import com.pazos.wtovrmanager.model.backendModels.Match;
import com.pazos.wtovrmanager.service.ApiService;
import com.pazos.wtovrmanager.service.WebSocketService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class RingPanel extends VBox {

    private final int ring;
    private final ApiService api;
    private final VBox matchList = new VBox(8);

    public RingPanel(String wsUrl, int ring, ApiService api) {
        this.ring = ring;
        this.api = api;

        Label lblRing = new Label("TAPIZ " + ring);
        lblRing.getStyleClass().add("ring-number");
        VBox header = new VBox(lblRing);
        header.getStyleClass().add("ring-header");

        WebSocketService ws = new WebSocketService(wsUrl);
        ws.addOnMessage(event -> {
            if ("MATCH_STARTED".equals(event.getAction())) reloadMatches();
        });
        ws.connect(ring);

        MatchCard liveCard = new MatchCard(ws);

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) ws.disconnect();
        });

        matchList.setStyle("-fx-padding: 12;");
        ScrollPane scroll = new ScrollPane(matchList);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent;");

        getStyleClass().add("ring-panel");
        setPrefWidth(260);
        getChildren().addAll(header, liveCard, scroll);

        reloadMatches();
    }

    private void reloadMatches() {
        Task<List<Match>> task = new Task<>() {
            @Override
            protected List<Match> call() throws Exception {
                return api.getMatchesRing(ring);
            }
        };
        task.setOnSucceeded(e -> showMatches(task.getValue()));
        new Thread(task).start();
    }

    private void showMatches(List<Match> matches) {
        matchList.getChildren().clear();
        matches.stream()
                .filter(m -> "available".equalsIgnoreCase(m.getStatus()))
                .forEach(m -> matchList.getChildren().add(buildCard(m)));
    }

    private VBox buildCard(Match m) {
        String blue   = m.getBlueAthlete() != null ? m.getBlueAthlete().getScoreboardName() : "-";
        String red    = m.getRedAthlete()  != null ? m.getRedAthlete().getScoreboardName()  : "-";
        String cat    = m.getCategory()    != null ? m.getCategory().getName()               : "-";
        String gender = m.getCategory()    != null && m.getCategory().getGender() != null
                        ? m.getCategory().getGender().substring(0, 1) : "?";
        String phase  = m.getPhase()       != null ? m.getPhase() : "-";

        Label lblNumber = new Label("#" + m.getMatchNumber());
        lblNumber.getStyleClass().add("label-match-number");

        Label lblMeta = new Label(phase + "  ·  " + gender);
        lblMeta.getStyleClass().add("label-subtitle");

        Label lblBlue = new Label(blue);
        lblBlue.getStyleClass().add("label-score-blue");

        Label lblVs = new Label("vs");
        lblVs.getStyleClass().add("label-subtitle");

        Label lblRed = new Label(red);
        lblRed.getStyleClass().add("label-score-red");

        Label lblCat = new Label(cat);
        lblCat.getStyleClass().add("label-section");

        VBox card = new VBox(4, lblNumber, lblMeta, lblBlue, lblVs, lblRed, lblCat);
        card.getStyleClass().add("card-match");
        return card;
    }
}
