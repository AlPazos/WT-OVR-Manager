package com.pazos.wtovrmanager.component;

import com.pazos.wtovrmanager.model.MatchEvent;
import com.pazos.wtovrmanager.service.WebSocketService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.function.Consumer;

public class MatchCard extends VBox {

    private final WebSocketService webSocket;
    private Consumer<String> onAction;
    @FXML
    private Label lblMatchNumber;
    @FXML
    private Label lblRound;
    @FXML
    private Label lblHomeName;
    @FXML
    private Label lblHomeCountry;
    @FXML
    private Label lblHomeScore;
    @FXML
    private Label lblAwayName;
    @FXML
    private Label lblAwayCountry;
    @FXML
    private Label lblAwayScore;
    @FXML
    private Label lblRoundTime;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblPenalties;

    public MatchCard(String baseWsUrl, int mat) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/pazos/wtovrmanager/component/match-card.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        webSocket = new WebSocketService(baseWsUrl);
        webSocket.setOnMessage(this::update);
        webSocket.setOnConnect(() -> setStatus("CONNECTED", "badge-available"));
        webSocket.setOnDisconnect(() -> setStatus("OFFLINE", "badge-available"));
        webSocket.connect(mat);

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) webSocket.disconnect();
        });
    }

    public void setOnAction(Consumer<String> onAction) {
        this.onAction = onAction;
    }

    @FXML
    private void initialize() {
    }

    private void update(MatchEvent event) {
        lblMatchNumber.setText("#" + event.getMatchNumber());
        lblRound.setText("R" + event.getRound());
        lblRoundTime.setText(event.getRoundTime());

        lblHomeName.setText(event.getHome().getName());
        lblHomeCountry.setText(event.getHome().getCountry());
        lblHomeScore.setText(String.valueOf(event.getScore().getHome()));

        lblAwayName.setText(event.getAway().getName());
        lblAwayCountry.setText(event.getAway().getCountry());
        lblAwayScore.setText(String.valueOf(event.getScore().getAway()));

        lblPenalties.setText(
                "P " + event.getPenalties().getHome() + " – " + event.getPenalties().getAway());

        updateCardStyle(event.getAction());
        if (onAction != null) onAction.accept(event.getAction());
    }

    private void updateCardStyle(String action) {
        getStyleClass().removeAll("card-match-active", "card-match-finished");
        switch (action) {
            case "MATCH_START":
            case "MATCH_TIME":
                getStyleClass().add("card-match-active");
                setStatus("LIVE", "badge-started");
                break;
            case "MATCH_END":
                getStyleClass().add("card-match-finished");
                setStatus("FINISHED", "badge-finished");
                break;
            default:
                setStatus("WAITING", "badge-available");
                break;
        }
    }

    private void setStatus(String text, String badgeClass) {
        lblStatus.setText(text);
        lblStatus.getStyleClass().removeAll("badge-available", "badge-started", "badge-finished");
        lblStatus.getStyleClass().add(badgeClass);
    }
}
