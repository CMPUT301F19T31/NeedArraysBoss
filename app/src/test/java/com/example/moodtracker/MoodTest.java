package com.example.moodtracker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoodTest {

    private Mood mockMood() {
        Mood mood = new Mood("happy", "alone", 1, "Weekend");
        return mood;
    }

    private Mood mockMoodCurrentTime() {
        Mood mood = new Mood("happy", "alone", System.currentTimeMillis(), "Weekend");
        return mood;
    }

    @Test
    void testToString() {
        Mood mood = mockMood();
        assertEquals("feeling happy\nbecause Weekend\nwas alone on 1", mood.toString());
    }

    @Test
    void testGetTimeAgo() {
        Mood mood = mockMoodCurrentTime();
        assertEquals("just now", mood.getTimeAgo());

    }


}

