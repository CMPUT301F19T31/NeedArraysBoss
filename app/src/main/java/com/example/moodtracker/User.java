package com.example.moodtracker;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {

    private String userID, email, password, phone;
    private ArrayList<Mood> moodHistory;
    private String imageurl;
    private String id;


    public User(String userID, String email, String password, ArrayList<Mood> moodHistory, String imageurl, String phone, String id) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.moodHistory = moodHistory;
        this.imageurl=imageurl;
        this.id=id;
    }

    public User(String userID, String email, String password, ArrayList<Mood> moodHistory) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = "";
        this.moodHistory = moodHistory;
        imageurl="";
        id="0";
    }


    public void User(String userID,String imageurl, String id, String email, String password) {
        this.userID = userID;
        this.id=id;
        this.imageurl=imageurl;
        this.email = email;
        this.password = password;
        this.phone = phone;
        moodHistory = null;
    }
  
    public void User(FirebaseUser firebaseUser) {
        this.email = firebaseUser.getEmail();
        this.password = "";
        this.imageurl=imageurl;
        this.id=id;
        this.userID = "";
    }
    public User(String userID, String email, String password) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = "";
        imageurl="";
        id="0";
        //moodHistory = null;
    }
    public User(String userID, String email, String password, String id, String phone, String imageurl) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.imageurl=imageurl;
        this.id=id;
        moodHistory = null;
    }
    public User(String userID, String password) {
        this.userID = userID;
        this.email = null;
        this.password = password;
        this.phone = null;
        moodHistory = null;
    }
    public User() {
        //do nothing
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    /*
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

 */

    public ArrayList<Mood> getMoodHistory() { return moodHistory; }
    public void setMoodHistory(ArrayList<Mood> moods) { moodHistory = moods; }
    public void addMood(Mood newMood) { moodHistory.add(0, newMood); }
    public void editMood(int index, Mood mood) { moodHistory.set(index, mood); }
    public void deleteMood(int index) { moodHistory.remove(index); }
}