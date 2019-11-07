package com.example.moodtracker;
import com.google.firebase.auth.FirebaseUser;

public class User {

    private String userID, email, password;

    public void User(String userID, String email, String password) {
        this.userID = userID;
        this.email = email;
        this.password = password;
    }

    public void User(FirebaseUser firebaseUser) {
        this.email = firebaseUser.getEmail();
        this.password = "";
        this.userID = "";
    }

    //There will be no setter for userID so that it cannot be changed
    public String getUserID() { return userID; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}