package com.example.moodtracker;

import java.util.ArrayList;

public class User {

    private String userID, email, password, phone;
    private ArrayList<Mood> moodHistory = new ArrayList<>();
    private String imageurl;
<<<<<<< Updated upstream
=======
    private String id;
    private ArrayList<String> friendList = new ArrayList<>();
>>>>>>> Stashed changes

    public void User(String userID,String imageurl, String id, String email, String password) {
        this.userID = userID;
        this.id=id;
        this.imageurl=imageurl;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
    public User(String userID, String email, String password) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = null;
    }
    public User(String userID, String password) {
        this.userID = userID;
        this.email = null;
        this.password = password;
        this.phone = null;
    }
    public User() {
        //do nothing
    }


    //There will be no setter for userID so that it cannot be changed
    public String getUserID() { return userID; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getImageurl() { return imageurl; }
    public String getPhone() { return phone; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }

    public ArrayList<Mood> getMoodHistory() { return moodHistory; }
    public void setMoodHistory(ArrayList<Mood> moods) { moodHistory = moods; }
    public ArrayList<String> getFriendList() { return friendList; }
    public void setFriendList(ArrayList<String> friends) { friendList = friends; }
}