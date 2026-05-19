package com.pazos.wtovrmanager.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class MatchEvent {
    private String matchNumber;
    @JsonAlias("eventType")
    private String action;
    private int round;
    private String roundTime;
    private ScorePair score;
    private ScorePair penalties;
    private Competitor home;
    private Competitor away;

    public String getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(String matchNumber) {
        this.matchNumber = matchNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getRoundTime() {
        return roundTime;
    }

    public void setRoundTime(String roundTime) {
        this.roundTime = roundTime;
    }

    public ScorePair getScore() {
        return score;
    }

    public void setScore(ScorePair score) {
        this.score = score;
    }

    public ScorePair getPenalties() {
        return penalties;
    }

    public void setPenalties(ScorePair penalties) {
        this.penalties = penalties;
    }

    public Competitor getHome() {
        return home;
    }

    public void setHome(Competitor home) {
        this.home = home;
    }

    public Competitor getAway() {
        return away;
    }

    public void setAway(Competitor away) {
        this.away = away;
    }
}
