package com.example.moodtracker;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.firestore.GeoPoint;

/**
 * This is a class that keeps track of a mood
 */

public class Mood {

    private String img;
    private String feeling, socialState, reason;
    private long date_time;
    private GeoPoint geo_point;
    private String friend = null;

    public Mood (String feeling, String socialState, long date_time, String reason, String image) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = reason;
        this.img = image;
    }

    public Mood (String feeling, String socialState, long date_time, String reason) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = reason;
        this.geo_point=null;
    }

    public Mood (String feeling, String socialState, long date_time, String reason, GeoPoint geo_point) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = reason;
        this.geo_point=geo_point;
    }

    public Mood (String feeling, String socialState, long date_time) {
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
    public String getFriend() { return friend; }
    public long getDate_time() {  return date_time; }
    public String getImg() {  return img; }

    public String getTimeAgo() {
        int second = 1000;
        int minute = 60 * second;
        int hour = 60 * minute;
        int day = 24 * hour;
        int month = 30 * day;

        long diff = System.currentTimeMillis() - date_time;
        if(diff < second)
            return "just now";
        else if(diff < 2*minute)
            return "a min ago";
        else if (diff < hour)
            return (diff/minute) + " minutes ago";
        else if (diff < 2*hour)
            return "an hour ago";
        else if (diff < day)
            return (diff/hour) + " hours ago";
        else if (diff < 2*day)
            return "a day ago";
        else if (diff < month)
            return (diff/day) + " days ago";
        else if(diff < 2*month)
            return "a month ago";
        else
            return (diff/month) + " months ago";

    }

    //setters
    public void setFeeling(String feeling) { this.feeling = feeling; }
    public void setSocialState(String socialState) { this.socialState = socialState; }
    public void setReason(String reason) { this.reason = reason; }
    public void setGeo_point(GeoPoint geo_point) { this.geo_point = geo_point; }
    public void setFriend(String friend) { this.friend = friend; }
    public void setImg(String img) { this.img = img; }
}