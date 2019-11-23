package com.example.moodtracker;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    private User mockUser() {
        ArrayList<Mood> moodList = new ArrayList<Mood>();
        User user = new User("1", "user@mymail.com", "abc123", moodList, "http://imgsvr.com/images/portrait.jpg", "0000000000");
        return user;
    }

    private Mood mockMood() {
        Mood mood = new Mood("happy", "alone", 1, "Weekend");
        return mood;
    }

    @Test
    void testGetMoodHistory() {
        User user = mockUser();
        ArrayList<Mood> moodHistory = user.getMoodHistory();
        assertNotEquals(null, moodHistory);
        assertEquals(0, moodHistory.size());
    }

    @Test
    void testSetMoodHistory() {
        User user = mockUser();
        ArrayList<Mood> moodHistory = user.getMoodHistory();
        assertNotEquals(null, moodHistory);
        user.setMoodHistory(null);
        // Replace empty ArrayList with null
        assertEquals(null, user.getMoodHistory());
    }


    // Removed Functions
    /*

    @Test
    void testAddMood() {
        User user = mockUser();
        ArrayList<Mood> moodHistory = user.getMoodHistory();
        // Assert MoodHistory is empty
        assertEquals(0, moodHistory.size());
        user.addMood(mockMood());
        // Assert MoodHistory has a new Mood added
        assertEquals(1, moodHistory.size());
        // Check added Mood
        assertEquals("feeling happy\nbecause Weekend\nwas alone on 05/29/2015 05:50", moodHistory.get(0).toString());
    }

    @Test
    void testDeleteMood() {
        User user = mockUser();
        ArrayList<Mood> moodHistory = user.getMoodHistory();
        // Assert MoodHistory is empty
        assertEquals(0, moodHistory.size());
        user.addMood(mockMood());
        // Assert MoodHistory has a new Mood added
        assertEquals(1, moodHistory.size());
        // Check added Mood
        assertEquals("feeling happy\nbecause Weekend\nwas alone on 05/29/2015 05:50", moodHistory.get(0).toString());
        // Delete Mood at index 0
        user.deleteMood(0);
        // Check Mood Deleted
        assertEquals(0, moodHistory.size());
    }

    @Test
    void testEditMood() {
        User user = mockUser();
        ArrayList<Mood> moodHistory = user.getMoodHistory();
        // Assert MoodHistory is empty
        assertEquals(0, moodHistory.size());
        user.addMood(mockMood());
        // Assert MoodHistory has a new Mood added
        assertEquals(1, moodHistory.size());
        // Check added Mood
        assertEquals("feeling happy\nbecause Weekend\nwas alone on 05/29/2015 05:50", moodHistory.get(0).toString());
        Mood newMood = new Mood("sad", "alone", "05/31/2015 05:50", "Monday");
        // Replace mood element with new mood
        user.editMood(0, newMood);
        // Check edited Mood
        assertEquals(1, moodHistory.size());
        assertEquals("feeling sad\nbecause Monday\nwas alone on 05/31/2015 05:50", moodHistory.get(0).toString());
    }


     */
}
