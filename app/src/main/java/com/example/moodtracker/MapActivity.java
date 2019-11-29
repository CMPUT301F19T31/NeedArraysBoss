package com.example.moodtracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * onMapReady
     * @param googleMap gets this as parameter and creates the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        addMapMarkers();

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private static final String TAG = "MapActivity";

    private boolean mLocationPermissionGranted = true; //false;
    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

    //vars
    private GoogleMap mMap;
    private ArrayList<String> friends;
    private User currentUser;
    private ArrayList<Mood> friendMoodHistory;
    private ArrayList<Mood> userMoods;
    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private DocumentReference docRef;
    private ArrayList<Mood> moods;
    private HashMap<String, Integer> moodEmojis;
    private int flag;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initMap();
        initEmoji();

        String str = getIntent().getStringExtra("flag");
        flag = Integer.parseInt(str); //1 is for friends mood and 0 is for the users mood
        Log.d(TAG, "MapActivity: flag = "+flag);

        friendMoodHistory = new ArrayList<>();
        friends = new ArrayList<>();
    }

    /**
     * initMap
     * initializes the map
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    /**
     * addMapMarkers
     * adds markers from the current users mood or the people the user follows, depending upon which tab the user is in
     */
    private void addMapMarkers() {

        if (mMap != null) {

            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(this, mMap);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterManagerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

            mAuth = FirebaseAuth.getInstance();
            docRef = FirebaseFirestore.getInstance().collection("users").document("user" + mAuth.getCurrentUser().getEmail());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUser = documentSnapshot.toObject(User.class);
                    //helpingAddMapMarker();
                    if(flag==0) {
                        userMoods = currentUser.getMoodHistory();
                        userId =currentUser.getUserID();
                        moods = userMoods;
                        helpingAddMapMarker();
                    }else{
                        getFriendList();
                    }
                }
            });
        }
    }

    /**
     * helpingAddMapMarker
     * Helper function called when the firebase returns the userdata. It
     */
    void helpingAddMapMarker(){
        Log.d(TAG, "MapActivity: helpingAddMapMarker started");

        for (Mood mood : moods) {
            if(mood.getGeo_point()!= null) {
                try {
                    String snippet = mood.getFeeling();
                    if (mood.getReason() != "") {
                        snippet = snippet + ": " + mood.getReason();
                    }

                    int avatar = moodEmojis.get(mood.getFeeling());

                    Log.d(TAG, "MapActivity: flag (helpingAddMapMarker) = "+flag);
                    if(flag==1){
                        userId=mood.getFriend();
                        Log.d(TAG, "MapActivity: helpingAddMapMarker friends latitude: " + mood.getGeo_point().getLatitude());
                        Log.d(TAG, "MapActivity: helpingAddMapMarker friends longitude: " + mood.getGeo_point().getLongitude());
                    }

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(mood.getGeo_point().getLatitude(), mood.getGeo_point().getLongitude()),
                            userId,
                            snippet,
                            avatar
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                } catch (NullPointerException e) {
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                }
            }

        }

        mClusterManager.cluster();
    }

    /**
     * Helper function to addMapMarkers. It connects to the database and retrieves the list
     * of users present in the database that the current user is friends with.
     */
    public void getFriendList() {
        for(int i=0; i<currentUser.getFollowingList().size(); i++)
            friends.add(currentUser.getFollowingList().get(i).getUser());
        refreshList();
    }

    public void refreshList() {
        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                friendMoodHistory.clear();
                for(DocumentSnapshot doc: data) {
                    User user = doc.toObject(User.class);
                    if(friends.contains(user.getEmail())) {
                        for(int i=0; i<user.getMoodHistory().size(); i++) {
                            Mood mood = user.getMoodHistory().get(i);
                            mood.setFriend(user.getUserID());
                            friendMoodHistory.add(mood);
                        }
                    }
                }
                moods = friendMoodHistory;
                helpingAddMapMarker();
            }
        });

    }

    /**
     * initEmoji
     * Initialises the emoji hashmap moodemojis. moodEmojis is a global variable that will be
     * used by other functions.
     */
    public void initEmoji() {
        //initializes emoji array
        moodEmojis = new HashMap<>();
        moodEmojis.put("happy", R.drawable.happy);
        moodEmojis.put("excited", R.drawable.excited);
        moodEmojis.put("hopeful", R.drawable.hopeful);
        moodEmojis.put("satisfied", R.drawable.satisfied);
        moodEmojis.put("sad", R.drawable.sad);
        moodEmojis.put("angry", R.drawable.angry);
        moodEmojis.put("frustrated", R.drawable.frustrated);
        moodEmojis.put("confused", R.drawable.confused);
        moodEmojis.put("annoyed", R.drawable.annoyed);
        moodEmojis.put("hopeless", R.drawable.hopeless);
        moodEmojis.put("lonely", R.drawable.lonely);
    }

    /**
     * getDeviceLocation
     * Gets the current location of the user
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /**
     * moveCamera
     * sets the camera of map to the latlng provided to the fuction
     *
     * @param latLng
     * get the latitude and longitude of the position where the camera should be set
     * @param zoom
     * gets how zoomed the camera should be when the camera set the the latlng
     */
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}