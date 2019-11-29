package com.example.moodtracker;

import java.util.ArrayList;

public class User {

    private String userID, email, profilePic;
    private int numFollwers=0;


    private ArrayList<Mood> moodHistory = new ArrayList<>();
    private ArrayList<Following> followingList = new ArrayList<>();
    private ArrayList<Notification> notification = new ArrayList<>();

    public User(String userID, String email, String profilePic) {
        this.userID = userID;
        this.profilePic = profilePic;
        this.email = email;
    }
    public User(String userID, String email) {
        this.userID = userID;
        this.email = email;
    }
    public User() {
        //do nothing
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) { this.userID = userID; }
    public String getEmail() {
        return email;
    }
    public String getProfilePic() {
        return profilePic;
    }
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public ArrayList<Mood> getMoodHistory() { return moodHistory; }
    public void setMoodHistory(ArrayList<Mood> moods) { moodHistory = moods; }
    public ArrayList<Following> getFollowingList() { return followingList; }
    public ArrayList<Notification> getNotification() { return notification; }

    public int getNumFollwers() {
        return numFollwers;
    }
    public void setNumFollwers(int numFollwers) {
        this.numFollwers = numFollwers;
    }
}