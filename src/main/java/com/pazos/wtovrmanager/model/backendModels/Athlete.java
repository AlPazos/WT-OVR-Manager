package com.pazos.wtovrmanager.model.backendModels;

public class Athlete {
    private String scoreboardName;
    private String givenName;
    private String familyName;
    private String gender;
    private Integer rank;
    private Integer seed;
    private String flagAbbreviation;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public String getFlagAbbreviation() {
        return flagAbbreviation;
    }

    public void setFlagAbbreviation(String flagAbbreviation) {
        this.flagAbbreviation = flagAbbreviation;
    }
}
