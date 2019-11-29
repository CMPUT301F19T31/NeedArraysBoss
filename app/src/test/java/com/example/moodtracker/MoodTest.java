package com.example.moodtracker;
import com.google.firebase.firestore.GeoPoint;

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

    private Mood mockMoodNoReason() {
        Mood mood = new Mood("happy", "alone", System.currentTimeMillis());
        return mood;
    }

    private Mood mockMoodNoArgs() {
        Mood mood = new Mood();
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

    @Test
    void testFeeling() {
        Mood mood = mockMoodNoReason();
        assertEquals("happy", mood.getFeeling());
        mood.setFeeling("sad");
        assertEquals("sad", mood.getFeeling());
    }

    @Test
    void testSocialState() {
        Mood mood = mockMoodNoArgs();
        mood.setSocialState("with one other person");
        assertEquals("with one other person", mood.getSocialState());
    }

    @Test
    void testReason() {
        Mood mood = mockMoodCurrentTime();
        assertEquals("Weekend", mood.getReason());
        mood.setReason("Weekday");
        assertEquals("Weekday", mood.getReason());
    }

    @Test
    void testFriend() {
        Mood mood = mockMoodCurrentTime();
        mood.setFriend("Ridwan");
        assertEquals("Ridwan", mood.getFriend());
    }

    @Test
    void testImg() {
        Mood mood = mockMoodCurrentTime();
        mood.setImg("IMGURL");
        assertEquals("IMGURL", mood.getImg());
    }

    @Test
    void testDateTime() {
        Mood mood = mockMood();
        assertEquals(1, mood.getDate_time());
    }

    @Test
    void testGeoPoint() {
        Mood mood = mockMoodCurrentTime();
        mood.setGeo_point(new GeoPoint(1,2));
        assertEquals(1, mood.getGeo_point().getLatitude());
        assertEquals(2, mood.getGeo_point().getLongitude());
    }


}

