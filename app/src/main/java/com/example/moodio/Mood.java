package com.example.moodio;

public class Mood {

    private String feeling, socialState, reason;

    public Mood (String feeling, String socialState, String reason) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.reason = reason;
    }
    public Mood (String feeling, String socialState) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.reason = "";
    }

    public String getFeeling() { return feeling; }
    public String getSocialState() { return socialState; }
    public String getReason() { return reason; }

    public void setFeeling(String feeling) { this.feeling = feeling; }
    public void setSocialState(String socialState) { this.socialState = socialState; }
    public void setReason(String reason) { this.reason = reason; }
}
