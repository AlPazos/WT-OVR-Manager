package com.pazos.wtovrmanager.component;

import animatefx.animation.*;
import com.pazos.wtovrmanager.I18n;
import com.pazos.wtovrmanager.model.backendModels.Athlete;
import com.pazos.wtovrmanager.model.backendModels.Category;
import com.pazos.wtovrmanager.model.backendModels.Match;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

public class SelectableCard<T> extends VBox {

    private static final String SELECTED_BLUE = "card-selectable-selected";
    private static final String SELECTED_RED  = "card-selectable-selected-red";

    public SelectableCard(T item, BiConsumer<T, SelectableCard<T>> onSelect) {
        getStyleClass().add("card-selectable");
        setSpacing(4);

        if (item instanceof Match m) {
            buildMatch(m);
        } else if (item instanceof Athlete a) {
            buildAthlete(a);
        } else if (item instanceof Category c) {
            buildCategory(c);
        }

        setOnMouseClicked(e -> onSelect.accept(item, this));
    }

    public void selectBlue() {
        getStyleClass().removeAll(SELECTED_BLUE, SELECTED_RED);
        getStyleClass().add(SELECTED_BLUE);
        new RubberBand(this).play();
    }

    public void selectRed() {
        getStyleClass().removeAll(SELECTED_BLUE, SELECTED_RED);
        getStyleClass().add(SELECTED_RED);
        new RubberBand(this).play();
    }

    public void deselect() {
        getStyleClass().removeAll(SELECTED_BLUE, SELECTED_RED);
        new Wobble(this).play();
    }

    public boolean isSelected() {
        return getStyleClass().contains(SELECTED_BLUE) || getStyleClass().contains(SELECTED_RED);
    }

    private void buildMatch(Match m) {
        Label lblNumber = new Label("#" + m.getMatchNumber());
        lblNumber.getStyleClass().add("label-match-number");

        String phase  = m.getPhase() != null ? I18n.getOrDefault("rounds." + m.getPhase(), m.getPhase()) : "-";
        String gender = m.getCategory() != null && m.getCategory().getGender() != null
                ? m.getCategory().getGender().substring(0, 1) : "?";
        Label lblMeta = new Label(phase + "  ·  " + gender);
        lblMeta.getStyleClass().add("label-subtitle");

        String blue = m.getBlueAthlete() != null ? m.getBlueAthlete().getScoreboardName() : "-";
        String red  = m.getRedAthlete()  != null ? m.getRedAthlete().getScoreboardName()  : "-";
        Label lblBlue = new Label(blue);
        lblBlue.getStyleClass().add("label-score-blue");

        Label lblVs = new Label(I18n.get("match.vs"));
        lblVs.getStyleClass().add("label-subtitle");

        Label lblRed = new Label(red);
        lblRed.getStyleClass().add("label-score-red");

        getChildren().addAll(lblNumber, lblMeta, lblBlue, lblVs, lblRed);

        if (m.getCategory() != null) {
            Label lblCat = new Label(m.getCategory().getName());
            lblCat.getStyleClass().add("label-section");
            getChildren().add(lblCat);
        }
    }

    private void buildAthlete(Athlete a) {
        Label lblName = new Label(a.getFullName());
        lblName.getStyleClass().add("ring-number");

        Label lblScoreboard = new Label(a.getScoreboardName());
        lblScoreboard.getStyleClass().add("label-subtitle");

        getChildren().addAll(lblName, lblScoreboard);
    }

    private void buildCategory(Category c) {
        Label lblName = new Label(c.getName());
        lblName.getStyleClass().add("ring-number");

        if (c.getGender() != null) {
            Label lblGender = new Label(c.getGender().equals("MALE") ? I18n.getOrDefault("gender.male", "?") : I18n.getOrDefault("gender.female", "?"));
            lblGender.getStyleClass().add("label-subtitle");
            getChildren().addAll(lblName, lblGender);
        } else {
            getChildren().add(lblName);
        }
    }
}
