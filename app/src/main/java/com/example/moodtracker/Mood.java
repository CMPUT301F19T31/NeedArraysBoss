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

    //setters
    public void setFeeling(String feeling) { this.feeling = feeling; }
    public void setSocialState(String socialState) { this.socialState = socialState; }
    public void setReason(String reason) { this.reason = reason; }
}