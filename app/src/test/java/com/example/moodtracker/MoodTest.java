package com.example.moodtracker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoodTest {

    private Mood mockMood() {
        Mood mood = new Mood("happy", "alone", "05/29/2015 05:50", "Weekend");
        return mood;
    }

    @Test
    void testToString() {
        Mood mood = mockMood();
        assertEquals("feeling happy\nbecause Weekend\nwas alone on 05/29/2015 05:50", mood.toString());
    }

}

