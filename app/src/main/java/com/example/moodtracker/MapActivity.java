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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        addMapMarkers();

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            //getLastKnownLocation();

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

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    ////////////////////////////////////////////////
    private boolean mLocationPermissionGranted = true; //false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient mFusedLocationClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    ////////////////////////////////////////////////
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

    //vars
    //private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private User user;
    private FirebaseUser currentUser;
    private ArrayList<Mood> moodHistory;
    private FirebaseAuth mAuth;
    private DocumentReference docRef;
    private ArrayList<Mood> moods;
    private HashMap<String, Integer> moodEmojis;
    //private Map<String,String> moodEmojis=new HashMap<String, String>();
    //HashMap<String, String> moodEmojis = new HashMap<String, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //getLocationPermission();
        //isMapsEnabled();
        initMap();
        initEmoji();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

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
            currentUser = mAuth.getCurrentUser();
            docRef = FirebaseFirestore.getInstance().collection("users").document("user" + mAuth.getCurrentUser().getEmail());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
                    helpingAddMapMarker();
                }
            });
            //helpingAddMapMarker();

            //moveCamera();
        }
    }

    /**
     * helpingAddMapMarker
     * Helper function called when the firebase returns the userdata. It
     */
    void helpingAddMapMarker(){
        moods = user.getMoodHistory();

        for (Mood mood : moods) {

            //Log.d(TAG, "addMapMarkers: location: " + mood.getGeo_point().toString());
            if(mood.getGeo_point()!=null) {
                try {
                    String snippet = mood.getFeeling() + ": ";
                    if (mood.getReason() != null) {
                        snippet = snippet + mood.getReason();
                    } else {
                        snippet = snippet + "no reason";
                    }

                    int avatar = moodEmojis.get(mood.getFeeling());

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(mood.getGeo_point().getLatitude(), mood.getGeo_point().getLongitude()),
                            user.getUserID(),
                            snippet,
                            avatar,
                            user
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
     * initEmoji
     * Initialises the emoji hashmap moodemojis. moodEmojis is a global variable that will be
     * used by other functions.
     */
    public void initEmoji() {
        //initializes emoji array
        moodEmojis = new HashMap<>();
        moodEmojis.put("happy", 0x1F601);
        moodEmojis.put("excited", 0x1F606);
        moodEmojis.put("hopeful", 0x1F60A);
        moodEmojis.put("satisfied", 0x1F60C);
        moodEmojis.put("sad", 0x1F61E);
        moodEmojis.put("angry", 0x1F621);
        moodEmojis.put("frustrated", 0x1F623);
        moodEmojis.put("confused", 0x1F635);
        moodEmojis.put("annoyed", 0x1F620);
        moodEmojis.put("hopeless",0x1F625);
        moodEmojis.put("lonely", 0x1F614);
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