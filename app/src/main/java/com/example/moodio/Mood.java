package com.example.moodio;

import androidx.annotation.NonNull;

public class Mood {

    private int img;
    private String feeling, socialState, reason, date_time;

    public Mood (String feeling, String socialState, String date_time, String reason) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = reason;
    }
    public Mood (String feeling, String socialState, String date_time) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = "";
    }

    public String toString() {
        String text = "feeling " + feeling + "\n";
        if (!reason.equals(""))
            text = text + "because " + reason + "\n";
        text = text + "was " + socialState + " on " + date_time;

        return text;
    }

    //getters
    public String getFeeling() { return feeling; }
    public String getSocialState() { return socialState; }
    public String getReason() { return reason; }

    //setters
    public void setFeeling(String feeling) { this.feeling = feeling; }
    public void setSocialState(String socialState) { this.socialState = socialState; }
    public void setReason(String reason) { this.reason = reason; }
}
