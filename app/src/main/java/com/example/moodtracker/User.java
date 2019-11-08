package com.example.moodtracker;
import com.google.firebase.auth.FirebaseUser;

public class User {

    private String id, userID, email, password;
    private String imageurl;

    public void User(String userID,String imageurl, String id, String email, String password) {
        this.userID = userID;
        this.id=id;
        this.imageurl=imageurl;
        this.email = email;
        this.password = password;
    }

    public void User(FirebaseUser firebaseUser) {
        this.email = firebaseUser.getEmail();
        this.password = "";
        this.imageurl=imageurl;
        this.id=id;
        this.userID = "";
    }

    //There will be no setter for userID so that it cannot be changed
    public String getUserID() { return userID; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getId() { return userID; }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}