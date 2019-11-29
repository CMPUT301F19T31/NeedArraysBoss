package com.example.moodtracker;

/**
 * Its an object that connects to the user they are following
 */
public class Following {

    private int type;
    // 1 = Following
    // 2 = Following only recent
    private String user;

    public Following()
    {
    }

    public Following(int type, String user)
    {
        this.type=type;
        this.user=user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}