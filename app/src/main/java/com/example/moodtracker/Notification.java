package com.example.moodtracker;

public class Notification {

    private int type;
    // 1 = Follow Req
    // 2 = Follow Accepted
    // 3 = Follow Denied
    // 4 = Unfollowed
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
        else if(type==2)
            return user1+" has accepted your follow request";
        else if(type==3)
            return user1+" has denied your follow request";
        else if(type==4)
            return user1+" is no longer following you";
        else
            return "No type match";
    }
}