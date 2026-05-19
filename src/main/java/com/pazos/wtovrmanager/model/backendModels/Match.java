package com.pazos.wtovrmanager.model.backendModels;

public class Match {
    private String matchNumber;
    private Integer mat;
    private String phase;
    private String status;
    private Athlete blueAthlete;
    private Athlete redAthlete;
    private Category category;

    public String getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(String matchNumber) {
        this.matchNumber = matchNumber;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMat() {
        return mat;
    }

    public void setMat(Integer mat) {
        this.mat = mat;
    }

    public Athlete getBlueAthlete() {
        return blueAthlete;
    }

    public void setBlueAthlete(Athlete blueAthlete) {
        this.blueAthlete = blueAthlete;
    }

    public Athlete getRedAthlete() {
        return redAthlete;
    }

    public void setRedAthlete(Athlete redAthlete) {
        this.redAthlete = redAthlete;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
