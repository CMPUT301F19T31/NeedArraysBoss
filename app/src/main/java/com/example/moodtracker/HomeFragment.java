package com.example.moodtracker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Dialog dialog;
    private String feeling = "", socialState = "";

    private RecyclerView rv;
    private ArrayList<Mood> moodHistory;
    private MoodListAdapter moodHistoryAdapter;
    private RecyclerView.LayoutManager moodHistoryLM;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "sample";
    private User user;
    private FloatingActionButton actn_btn;
    private FloatingActionButton btnMap;
    private FloatingActionButton button_search;
    //Button button_search;

    //private static final String TAG = "HomeFragment";
    private boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(getContext());
        EmojiCompat.init(config);

        //checkMapServices();
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //init();
        //private Button button_search;
        //Button button_search=root.findViewById(R.id.search);
        dialog = new Dialog(getContext());
        moodHistory = new ArrayList<Mood>();
        actn_btn = root.findViewById(R.id.addMoodEvent);
        button_search= root.findViewById(R.id.search);
        actn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMoodEvent(v);
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), Search_activity.class);

                startActivity(intent);
            }
        });

        /*
        btnMap = root.findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMapServices()){
                    if(mLocationPermissionGranted){
                        //getChatrooms(); HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
                        Intent intent = new Intent(getActivity(), MapActivity.class);
                        startActivity(intent);
                        //getLastKnownLocation();
                        //getUserDetails();
                    }
                    else{
                        getLocationPermission();
                    }
                }
                //Intent intent = new Intent(getActivity(), MapActivity.class);
                //startActivity(intent);
            }
        });
         */



        btnMap = root.findViewById(R.id.btnMap);
        init();
        /*
        if(checkMapServices()){
            init();
            /*
            btnMap = root.findViewById(R.id.btnMap);
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if(checkMapServices()){
                        if(mLocationPermissionGranted){
                            //getChatrooms(); HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
                            Intent intent = new Intent(getActivity(), MapActivity.class);
                            startActivity(intent);
                            //getLastKnownLocation();
                            //getUserDetails();
                        }
                        else{
                            getLocationPermission();
                        }
                    }
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    startActivity(intent);
                }
            });
        }
         */

        //initialize recyclerview
        rv = root.findViewById(R.id.moodList);
        moodHistoryLM = new LinearLayoutManager(getContext());
        moodHistoryAdapter = new MoodListAdapter(moodHistory);
        rv.setLayoutManager(moodHistoryLM);
        rv.setAdapter(moodHistoryAdapter);

        //start login activity
        moodHistoryAdapter.setOnClickListener(new MoodListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EditMoodEvent.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();
        userRef = null;

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

        if(currentUser == null) { // not signed in
            //start login activity
            Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else {
            onResume();
        }
    }

    public void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            userRef = FirebaseFirestore.getInstance().collection("users").document("user" + currentUser.getEmail());
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot == null) { return; }
                    user = documentSnapshot.toObject(User.class);
                    loadDataFromDB();
                }
            });
        }
    }


    public void loadDataFromDB() {
        if(user == null || user.getMoodHistory() == null) { return; }
        int size=user.getMoodHistory().size();

        moodHistory.clear();
        for(int i=0; i<size; i++)
        {
            moodHistory.add(user.getMoodHistory().get(i));
        }
        moodHistoryAdapter.notifyDataSetChanged();
    }




    public void createMoodEvent(View view) {
        dialog.setContentView(R.layout.add_mood_event); //opens the pop window

        Spinner feelingSpinner = (Spinner) dialog.findViewById(R.id.feelingSpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.feelings, R.layout.spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        feelingSpinner.setAdapter(adapter1);
        feelingSpinner.setOnItemSelectedListener(this);

        Spinner socialStateSpinner = (Spinner) dialog.findViewById(R.id.socialStateSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.socialStates, R.layout.spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        socialStateSpinner.setAdapter(adapter2);
        socialStateSpinner.setOnItemSelectedListener(this);

        Button addEventBtn = dialog.findViewById(R.id.addMoodEvent);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmojiEditText et = dialog.findViewById(R.id.reasonET);
                String reason = et.getText().toString();

                if(!feeling.equals("")) {
                    Mood newMood;

                    if (reason == null) {
                        newMood = new Mood(feeling, socialState, System.currentTimeMillis());
                    } else {
                        newMood = new Mood(feeling, socialState, System.currentTimeMillis(), reason);
                    }
                    moodHistory.add(0, newMood); //inserts new mood at the beginning of list
                    moodHistoryAdapter.notifyDataSetChanged();
                    user.setMoodHistory(moodHistory);
                    userRef.set(user);  // save to db

                    feeling = "";
                    socialState = "";
                    dialog.dismiss();   //closes the pop up window

                } else if (feeling.equals("")) {
                    Toast.makeText(dialog.getContext(), "Please select how you feel", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button clearEventBtn = dialog.findViewById(R.id.clearMoodEvent);
        clearEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.feelingSpinner)
            feeling = adapterView.getItemAtPosition(i).toString();
        else if(adapterView.getId() == R.id.socialStateSpinner)
            socialState = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    private void getUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
            DocumentReference userRef = db.collection("Users")
                    .document(FirebaseAuth.getInstance().getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);
                        getLastKnownLocation();
                    }
                }
            });
        }
        else{
            getLastKnownLocation();
        }
    }
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
                    mUserLocation.setGeo_point(geoPoint);
                    //mUserLocation.setTimestamp(null);
                    saveUserLocation();
                }
            }
        });
    }
    private void saveUserLocation(){
        if(mUserLocation != null){
            DocumentReference locationRef = db
                    .collection("UserLocation")
                    .document(FirebaseAuth.getInstance().getUid());
            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }
    }
*/


    private void init(){
        //FloatingActionButton btnMap = (FloatingActionButton) dialog.findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMapServices()){
                    if(mLocationPermissionGranted){
                        //getChatrooms(); HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
                        Intent intent = new Intent(getActivity(), MapActivity.class);
                        startActivity(intent);
                        //getLastKnownLocation();
                        //getUserDetails();
                    }
                    else{
                        getLocationPermission();
                    }
                }
                //Intent intent = new Intent(getActivity(), MapActivity.class);
                //startActivity(intent);
            }
        });


    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getChatrooms(); HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
            //init();
            //getLastKnownLocation();
            //getUserDetails();
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        //mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {

                final Task location = mFusedLocationClient.getLastLocation(); //mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            //      DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    /*
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    //getChatrooms(); HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
                    init();
                    //getLastKnownLocation();
                    //getUserDetails();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}