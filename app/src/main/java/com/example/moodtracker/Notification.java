package com.example.moodtracker;

public class Notification {

    private int type;
    // 1 = Follow Req
    private String user1, user2;

    public Notification()
    {
    }

    public Notification(int type, String user1, String user2)
    {
        this.type=type;
        this.user1=user1;
        this.user2=user2;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    String getString(){
        if(type==1)
            return user1+" has requested to follow your moods";
        else
            return "No type match";
    }
}