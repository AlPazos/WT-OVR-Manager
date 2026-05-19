package com.pazos.wtovrmanager.model.backendModels;

public class Athlete {
    private String scoreboardName;
    private String givenName;
    private String familyName;

    public String getScoreboardName() {
        return scoreboardName;
    }

    public void setScoreboardName(String scoreboardName) {
        this.scoreboardName = scoreboardName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    public String getFullName() {
        return givenName + " " + familyName;
    }
}
