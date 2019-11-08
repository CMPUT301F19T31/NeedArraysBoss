package com.example.moodtracker;
public class User {

    private String userID, email, password, phone;

    public void User(String userID, String email, String password, String phone) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
    public void User(String userID, String email, String password) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.phone = null;
    }
    public void User(String userID, String password) {
        this.userID = userID;
        this.email = null;
        this.password = password;
        this.phone = null;
    }
    public void User() {
        //do nothing
    }


    //There will be no setter for userID so that it cannot be changed
    public String getUserID() { return userID; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    public void setPhone(String phone) { this.phone = phone; }
}