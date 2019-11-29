package com.example.moodtracker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FollowingTest {

    private Following mockFollowing() {
        Following following = new Following(1, "ridwan@mood.com");
        return following;
    }

    private Following mockFollowingNoArgs() {
        Following following = new Following();
        return following;
    }

    @Test
    void testSetUser() {
        Following follow = mockFollowing();
        assertEquals("ridwan@mood.com", follow.getUser());
        follow.setUser("andalib@mood.com");
        assertEquals("andalib@mood.com", follow.getUser());
    }

    @Test
    void testGetUser() {
        Following follow = mockFollowing();
        assertEquals("ridwan@mood.com", follow.getUser());
    }

    @Test
    void testGetType() {
        Following follow = mockFollowing();
        assertEquals("ridwan@mood.com", follow.getUser());
        follow.setUser("andalib@mood.com");
        assertEquals(1, follow.getType());
    }

    @Test
    void testNoArgsFollowing() {
        Following follow = mockFollowingNoArgs();
        follow.setUser("andalib@mood.com");
        assertEquals("andalib@mood.com", follow.getUser());
    }


}

