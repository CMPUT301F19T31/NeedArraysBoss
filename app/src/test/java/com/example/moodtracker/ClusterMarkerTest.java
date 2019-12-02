package com.example.moodtracker;
import com.example.moodtracker.ClusterMarker;
import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClusterMarkerTest {

    private ClusterMarker mockClusterMarker() {
        ClusterMarker clusterMarker = new ClusterMarker(mockLatLng(), "title", "reason", 1);
        return clusterMarker;
    }

    private LatLng mockLatLng(){
        LatLng latLng = new LatLng(1,1);
        return latLng;
    }


    @Test
    void testClusterMarker() {
        ClusterMarker clusterMarker = mockClusterMarker();
        assertEquals(1,clusterMarker.getIconPicture());
        clusterMarker.setIconPicture(2);
        assertEquals(2,clusterMarker.getIconPicture());
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

