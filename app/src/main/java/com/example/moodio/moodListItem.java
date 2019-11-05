package com.example.moodio;

public class moodListItem {
    private int img;
    private String feeling, socialState, reason, date_time;

    public moodListItem(Mood mood) {
        feeling = mood.getFeeling();
        socialState = mood.getSocialState();
        reason = mood.getReason();
    }
}
