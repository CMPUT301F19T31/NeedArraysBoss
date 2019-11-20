package com.example.moodtracker;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    private User mockUser() {
        ArrayList<Mood> moodList = new ArrayList<Mood>();
        User user = new User("1", "user@mymail.com", "abc123", moodList, "http://imgsvr.com/images/portrait.jpg", "0000000000", "1");
        return user;
    }

    private Mood mockMood() {
        Mood mood = new Mood("happy", "alone", "05/29/2015 05:50", "Weekend");
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
}

/*
    @Test
    void testAdd() {
        CityList cityList = mockCityList();
        assertEquals(1, cityList.getCities().size());
        City city = new City("Regina", "Saskatchewan");
        cityList.add(city);
        assertEquals(2, cityList.getCities().size());
        assertTrue(cityList.getCities().contains(city));
    }

    @Test
    void testAddException() {
        CityList cityList = mockCityList();
        City city = new City("Yellowknife", "Northwest Territories");
        cityList.add(city);
        assertThrows(IllegalArgumentException.class, () -> {
            cityList.add(city);
        });
    }

    @Test
    void testGetCities() {
        CityList cityList = mockCityList();
        assertEquals(0, mockCity().compareTo(cityList.getCities().get(0)));
        City city = new City("Charlottetown", "Prince Edward Island");
        cityList.add(city);
        assertEquals(0, city.compareTo(cityList.getCities().get(0)));
        assertEquals(0, mockCity().compareTo(cityList.getCities().get(1)));
    }

    @Test
    void testHasCity() {
        CityList cityList = mockCityList();
        City city = new City("Charlottetown", "Prince Edward Island");
        assertEquals(false, cityList.hasCity(city));
        cityList.add(city);
        assertEquals(true, cityList.hasCity(city));
    }

    @Test
    void testDelete() {
        CityList cityList = mockCityList();
        assertEquals(1, cityList.getCities().size());
        City city = new City("Regina", "Saskatchewan");
        cityList.add(city);
        assertEquals(2, cityList.getCities().size());
        assertTrue(cityList.getCities().contains(city));
        cityList.delete(city);
        assertEquals(1, cityList.getCities().size());
        assertFalse(cityList.getCities().contains(city));
    }

    @Test
    void testDeleteException() {
        CityList cityList = mockCityList();
        City city = new City("Yellowknife", "Northwest Territories");
        assertThrows(IllegalArgumentException.class, () -> {
            cityList.delete(city);
        });
    }

    @Test
    void testCountCities() {
        CityList cityList = mockCityList();
        assertEquals(1, cityList.countCities());
        City city = new City("Yellowknife", "Northwest Territories");
        cityList.add(city);
        assertEquals(2, cityList.countCities());
        cityList.delete(city);
        assertEquals(1, cityList.countCities());
    }
}
*/