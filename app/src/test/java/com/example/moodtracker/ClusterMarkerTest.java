package com.example.moodtracker;
import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClusterMarkerTest {

    private ClusterMarker mockClusterMarker() {
        ClusterMarker clusterMarker = new ClusterMarker(mockLatLng(), "title", "reason", 1, mockUser());
        return clusterMarker;
    }

    private LatLng mockLatLng(){
        LatLng latLng = new LatLng(1,1);
        return latLng;
    }

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

    private User mockUserWithPic() {
        User user = new User("test", "test@mood.com", "IMGURL");
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
    void testClusterMarker() {
        ClusterMarker clusterMarker = mockClusterMarker();
        assertEquals(1,clusterMarker.getIconPicture());
        clusterMarker.setIconPicture(2);
        assertEquals(2,clusterMarker.getIconPicture());
        clusterMarker.setUser(mockUserWithPic());
        assertEquals("test@mood.com",clusterMarker.getUser().getEmail());
        clusterMarker.setPosition(new LatLng(4,4));
        assertEquals(4,clusterMarker.getPosition().longitude);
        assertEquals("title",clusterMarker.getTitle());
        clusterMarker.setTitle("title2");
        assertEquals("title2",clusterMarker.getTitle());
        assertEquals("reason",clusterMarker.getSnippet());
        clusterMarker.setSnippet("reason2");
        assertEquals("reason2",clusterMarker.getSnippet());

    }



}

