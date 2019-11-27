package com.example.moodtracker;

import com.google.firebase.firestore.GeoPoint;

/**
 * This is a class that keeps track of a mood
 */

public class Mood {

    private int img;
    private String feeling, socialState, reason, date_time;
    private GeoPoint geo_point;

    /*public Mood (String feeling, String socialState, String date_time, String reason) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = reason;
        this.geo_point=null;
    }

    public Mood (String feeling, String socialState, String date_time, String reason, GeoPoint geo_point) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = reason;
        this.geo_point=geo_point;
    }*/

    public Mood (String feeling, String socialState, String date_time) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = "";
        this.geo_point= null;
    }
    public Mood () {
        //do nothing!
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
    public GeoPoint getGeo_point() {
        return geo_point;
    }

    //setters
    public void setFeeling(String feeling) { this.feeling = feeling; }
    public void setSocialState(String socialState) { this.socialState = socialState; }
    public void setReason(String reason) { this.reason = reason; }
    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }
}