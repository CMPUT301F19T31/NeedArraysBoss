package com.example.moodtracker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FollowingTest {

    private Following mockFollowing() {
        Following following = new Following(1, "ridwan@mood.com");
        return following;
    }

    @Test
    void testSetUser() {
        Following follow = mockFollowing();
        assertEquals("ridwan@mood.com", follow.getUser());
        follow.setUser("andalib@mood.com");
        assertEquals("andalib@mood.com", follow.getUser());
    }
}

