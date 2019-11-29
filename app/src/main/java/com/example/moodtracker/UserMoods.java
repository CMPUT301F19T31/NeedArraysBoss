package com.example.moodtracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class UserMoods extends Fragment implements AdapterView.OnItemSelectedListener {

    private Dialog dialog;
    private String feeling = "", socialState = "";

    private RecyclerView rv;
    private ArrayList<Mood> moodHistory;
    private ArrayList<Mood> filterMoodHistory;
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
    private String image;

    //private static final String TAG = "HomeFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    //private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean getmap = false;
    GeoPoint geoPoint;
    //boolean gotRecentLocation=false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_moods, container, false);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(getContext());
        EmojiCompat.init(config);

        image = null;
        dialog = new Dialog(getContext());
        moodHistory = new ArrayList<Mood>();
        actn_btn = root.findViewById(R.id.addMoodEvent);
        actn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMoodEvent(v);
            }
        });

        //initialise the mood filter spinner
        Spinner moodFilterSpinner = root.findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.feelings, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        moodFilterSpinner.setAdapter(adapter);
        moodFilterSpinner.setOnItemSelectedListener(this);

        btnMap = root.findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getmap=true;
                init();
            }
        });

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

    public void filterMoodList(String feeling) {
        filterMoodHistory = new ArrayList<>();
        if(feeling.equals("")) {
            moodHistoryAdapter.setList(moodHistory);
            moodHistoryAdapter.notifyDataSetChanged();
        } else {
            for(int i = 0; i < moodHistory.size(); i++) {
                if(moodHistory.get(i).getFeeling().equals(feeling))
                    filterMoodHistory.add(moodHistory.get(i));
            }
            moodHistoryAdapter.setList(filterMoodHistory);
            moodHistoryAdapter.notifyDataSetChanged();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if(checkMapServices()) {
            if(mLocationPermissionGranted) {
                if (getmap) {
                    init();
                }
                getDeviceLocation();
            }
        }else{
            getLocationPermission();
        }

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseFirestore.getInstance().collection("users").document("user" + currentUser.getEmail());
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot == null) {
                        return;
                    }
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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.feelingSpinner)
            feeling = adapterView.getItemAtPosition(i).toString();
        else if (adapterView.getId() == R.id.socialStateSpinner)
            socialState = adapterView.getItemAtPosition(i).toString();
        else if (adapterView.getId() == R.id.filterSpinner)
            filterMoodList(adapterView.getItemAtPosition(i).toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    public void createMoodEvent(View view) {
        dialog.setContentView(R.layout.add_mood_event);

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

        final CheckBox enableMap = dialog.findViewById(R.id.enableMap);

        Button addEventBtn = dialog.findViewById(R.id.addMoodEvent);

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "createMoodEvent: mLocationPermissionGranted " + mLocationPermissionGranted);

                if (enableMap.isChecked() && !mLocationPermissionGranted) {
                    Toast.makeText(getContext(), "Permission not Granted", Toast.LENGTH_SHORT).show();
                    enableMap.setChecked(false);
                    if (checkMapServices()) {
                        getLocationPermission();
                    }
                } else {
                    EmojiEditText et = dialog.findViewById(R.id.reasonET);
                    String reason = et.getText().toString();

                    if (!feeling.equals("")) {
                        Mood newMood;
                        newMood = new Mood(feeling, socialState, System.currentTimeMillis());
                        if (reason != null) {
                            newMood.setReason(reason);
                        }
                        Log.d(TAG, "createMoodEvent: enableMap" + enableMap.isChecked());
                        if (enableMap.isChecked()) {
                            getDeviceLocation();
                            Log.d(TAG, "mapon: latitude: " + geoPoint.getLatitude());
                            Log.d(TAG, "mapon: longitude: " + geoPoint.getLongitude());
                            newMood.setGeo_point(geoPoint);
                            geoPoint=null;
                        }
                        if (image != null) {
                            newMood.setImg(image);
                        }

                        moodHistory.add(0, newMood); //inserts new mood at the beginning of list
                        moodHistoryAdapter.notifyDataSetChanged();
                        user.setMoodHistory(moodHistory);
                        userRef.set(user);  // save to db

                        feeling = "";
                        socialState = "";
                        image = null;
                        geoPoint = null;
                        dialog.dismiss();   //closes the pop up window

                    } else if (feeling.equals("")) {
                        Toast.makeText(dialog.getContext(), "Please select how you feel", Toast.LENGTH_LONG).show();
                    }

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

        TextView imageTV = dialog.findViewById(R.id.imageTV);
        imageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
            }
        });

        dialog.show();  //opens the pop window
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * init
     * Initializes the map if map services are working, and location permission is granted.
     */
    private void init() {
        Log.d(TAG, "init: mLocationPermissionGranted " + mLocationPermissionGranted);
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                getmap = false;
            } else {
                getLocationPermission();
            }
        }

    }

    /**
     * getDeviceLocation
     * gets the current device location
     */
    private void getDeviceLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Log.d(TAG, "getLastKnownLocation: called.");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());
                }
            }
        });
    }

    /**
     * checkMapServices
     * checks if isMapsEnabled and isMapsEnabled true
     * @return true if the google services are up-to-date and the gps is enabled
     */
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    /**
     * buildAlertMessageNoGps
     * gives an alert message that the gps is not enabled and gives the user option to go and enable it in the phone setting
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    /**
     * isMapsEnabled
     * Checks if GPS is enabled on the phone
     * @return true if GPS is enabled otherwise buildAlertMessageNoGps function is called
     */
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    /**
     * getLocationPermission
     * gets Location permission from the user
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            if(getmap){
                init();
            }
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * isServicesOk
     * Checks if google services are installed/up-to-date.
     * @return true if up-to-date
     */
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

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
                    //getChatrooms();
                    if(getmap){
                        init();
                    }
                    getDeviceLocation();
                }
                else{
                    getLocationPermission();
                }
            }
            case 3: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri image = data.getData();
                    try {
                        Bitmap temp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        temp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        int num = 50;
                        while (byteArray.length > 10000 && num > 0) {   // compress image to not more than 10 kb
                            byteArrayOutputStream.flush();
                            byteArrayOutputStream.reset();

                            temp.compress(Bitmap.CompressFormat.JPEG, num, byteArrayOutputStream);
                            num = num / 2;
                            byteArray = byteArrayOutputStream.toByteArray();
                        }
                        this.image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        Toast.makeText(dialog.getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

