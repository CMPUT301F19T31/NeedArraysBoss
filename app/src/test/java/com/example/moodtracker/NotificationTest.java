package com.example.moodtracker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

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

    private Notification mockNotificationNoArgs() {
        Notification notification = new Notification();
        return notification;
    }

    @Test
    void testFollowRequestNotification() {
        Notification notification = followRequestNotification();
        assertEquals("ridwan@mood.com has requested to follow your moods",notification.getString());
    }

    @Test
    void testAcceptedNotification() {
        Notification notification = acceptedNotification();
        assertEquals("ridwan@mood.com has accepted your follow request",notification.getString());
    }

    @Test
    void testDeniedNotification() {
        Notification notification = deniedNotification();
        assertEquals("ridwan@mood.com has denied your follow request",notification.getString());
    }

    @Test
    void testNoArgsNotification() {
        Notification notification = mockNotificationNoArgs();
        notification.setType(1);
        notification.setUser1("test@mood.com");
        assertEquals(1,notification.getType());
        assertEquals("test@mood.com",notification.getUser1());
    }
}

