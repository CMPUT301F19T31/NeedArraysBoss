package com.example.moodtracker;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    private Notification followRequestNotification() {
        Notification notification = new Notification(1, "ridwan@mood.com", "andalib@mood.com");
        return notification;
    }

    private Notification acceptedNotification() {
        Notification notification = new Notification(2, "ridwan@mood.com", "andalib@mood.com");
        return notification;
    }

    private Notification deniedNotification() {
        Notification notification = new Notification(3, "ridwan@mood.com", "andalib@mood.com");
        return notification;
    }

    private Notification unfollowedNotification() {
        Notification notification = new Notification(4, "ridwan@mood.com", "andalib@mood.com");
        return notification;
    }

    private Mood mockMood() {
        Mood mood = new Mood("happy", "alone", 1, "Weekend");
        return mood;
    }

    private Following mockFollowing() {
        Following following = new Following(1, "ridwan@mood.com");
        return following;
    }

    private User mockUser() {
        User user = new User("test", "test@mood.com");
        user.getFollowingList().add(mockFollowing());
        user.getMoodHistory().add(mockMood());
        user.getNotification().add(followRequestNotification());
        user.getNotification().add(acceptedNotification());
        user.getNotification().add(deniedNotification());
        user.getNotification().add(unfollowedNotification());
        user.setNumFollwers(1);
        return user;
    }

    @Test
    void testMoodHistory() {
        User user = mockUser();
        assertEquals(1, user.getMoodHistory().size());
        assertEquals("feeling happy\nbecause Weekend\nwas alone on 1", user.getMoodHistory().get(0).toString());
    }

    @Test
    void testNotification() {
        User user = mockUser();
        assertEquals(4, user.getNotification().size());
        assertEquals("ridwan@mood.com has requested to follow your moods", user.getNotification().get(0).getString());
        assertEquals("ridwan@mood.com has accepted your follow request", user.getNotification().get(1).getString());
        assertEquals("ridwan@mood.com has denied your follow request", user.getNotification().get(2).getString());
        assertEquals("ridwan@mood.com is no longer following you", user.getNotification().get(3).getString());
    }

    @Test
    void testFollowing() {
        User user = mockUser();
        assertEquals(1, user.getFollowingList().size());
        assertEquals("ridwan@mood.com", user.getFollowingList().get(0).getUser());
        assertEquals(1, user.getFollowingList().get(0).getType());
    }

}
