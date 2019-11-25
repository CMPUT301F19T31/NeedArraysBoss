package com.example.moodtracker;

/**
 * This is a class that keeps track of a mood
 */
public class Mood {

    private int img;
    private String feeling, socialState, reason, date_time;

    /**
     * This is the constructor including the optional reason argument
     *  @param feeling
     *  The mood of the mood event
     *  @param socialState
     *  Who the user is with
     *  @param date_time
     *  The timestamp
     */
    public Mood (String feeling, String socialState, String date_time, String reason) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = reason;
    }

    /**
     * This the constructor with 4 arguments
     * @param feeling
     * The mood of the mood event
     * @param socialState
     * Who the user is with
     * @param date_time
     * The timestamp
     */
    public Mood (String feeling, String socialState, String date_time) {
        this.feeling = feeling;
        this.socialState = socialState;
        this.date_time = date_time;
        this.reason = "";
    }

    /**
     * This the no argument constructor
     */
    public Mood () {
        //do nothing!
    }

    /**
     * This method converts the mood to a readable string
     * @return
     * Returns the readable string
     */
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
    public long getDate_time() { return date_time;}

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
}
