package com.example.moodtracker;



import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A fragment that displays a list of mood events created by the user's following list
 */
public class FollowingMoods extends Fragment implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private User currentUser;
    private ArrayList<User> allUsers;
    private ArrayList<String> friends;
    private FloatingActionButton btnMap;

    private RecyclerView rv;
    private ArrayList<Mood> friendMoodHistory;
    private ArrayList<Mood> filterFriendMoodHistory;
    private MoodListAdapter friendMoodHistoryAdapter;
    private RecyclerView.LayoutManager rvLM;

    private boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private boolean getmap = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_following_moods, container, false);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(getContext());
        EmojiCompat.init(config);

        FrameLayout frame = root.findViewById(R.id.socialStateSpinner2);

        // initialise variables
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null)
            return root;
        userRef = FirebaseFirestore.getInstance().collection("users");

        btnMap = root.findViewById(R.id.btnMap2);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getmap=true;
                init();
            }
        });

        friends = new ArrayList<>();
        rv = root.findViewById(R.id.moodList);
        allUsers = new ArrayList<>();
        friendMoodHistory = new ArrayList<>();
        friendMoodHistoryAdapter = new MoodListAdapter(friendMoodHistory);
        rvLM = new LinearLayoutManager(getContext());
        rv.setAdapter(friendMoodHistoryAdapter);
        rv.setLayoutManager(rvLM);

        //initialise the mood filter spinner
        Spinner moodFilterSpinner = root.findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.feelings, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        moodFilterSpinner.setAdapter(adapter);
        moodFilterSpinner.setOnItemSelectedListener(this);

        //start view mood fragment
        friendMoodHistoryAdapter.setOnClickListener(new MoodListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                String friendUsername = friendMoodHistory.get(index).getFriend();
                String email = null;
                int i = -1;
                for(User user: allUsers) {
                    if(user.getUserID().equals(friendUsername)) {
                        email = user.getEmail();
                        i = user.getMoodHistory().indexOf(friendMoodHistory.get(index));
                    }
                }

                //Start fragment
                Bundle args = new Bundle();
                args.putString("email", email);
                args.putInt("index", i);
                Fragment fragment = new ViewMood();
                fragment.setArguments(args);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack(null);
                transaction.commit();
            }
        });


        getFriendList();

        return root;
    }

    /**
     * Helper function to onCreateView. It connects to the database and retrieves the list
     * of users present in the database that the current user is friends with.
     */
    public void getFriendList() {
        userRef.document("user"+mAuth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(mAuth.getCurrentUser()==null)
                    return;
                currentUser = documentSnapshot.toObject(User.class);
                for(int i=0; i<currentUser.getFollowingList().size(); i++)
                    friends.add(currentUser.getFollowingList().get(i).getUser());
                refreshList();
            }
        });
    }

    /**
     * This is a helper function to getFriendList. It uses the global user variable and fills UI
     * with the appropriate information
     */
    public void refreshList() {
        userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                friendMoodHistory.clear();
                allUsers.clear();
                for(DocumentSnapshot doc: data) {
                    User user = doc.toObject(User.class);
                    
                    if(friends.contains(user.getEmail())) {
                        allUsers.add(user);
                        for(int i=0; i<user.getMoodHistory().size(); i++) {
                            Mood mood = user.getMoodHistory().get(i);
                            mood.setFriend(user.getUserID());
                            friendMoodHistory.add(mood);
                        }
                    }
                }
                sortFriendList();
                friendMoodHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * sortFriendList
     * Helper function to refreshList. Sorts the friendMoodHistory in order of newest to oldest
     * when all the moods of the a user is provided.
     */
    public void sortFriendList() {

    }

    /**
     * Called by the onItemSelected function when the filter spinner is used.
     * @param feeling is a string that contains the value of the spinner item selected by the user
     */
    public void filterMoodList(String feeling) {
        filterFriendMoodHistory = new ArrayList<>();
        if(feeling.equals("")) {
            friendMoodHistoryAdapter.setList(friendMoodHistory);
            friendMoodHistoryAdapter.notifyDataSetChanged();
        } else {
            for(int i = 0; i < friendMoodHistory.size(); i++) {
                if(friendMoodHistory.get(i).getFeeling().equals(feeling))
                    filterFriendMoodHistory.add(friendMoodHistory.get(i));
            }
            friendMoodHistoryAdapter.setList(filterFriendMoodHistory);
            friendMoodHistoryAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This is a required method for implementing AdapterView.OnItemClickListener. This function
     * recieves the item that was selected by the user, and chooses the appropriate action to save
     * the result.
     * @param parent is the object that notifies the progrom which spinner widget was used
     * @param view returns the view object
     * @param position is the index of the spinner item that was selected
     * @param id returns a long number
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.filterSpinner)
            filterMoodList(parent.getItemAtPosition(position).toString());
    }

    /**
     * This is a required method for implementing AdapterView.OnItemClickListener. This function
     * empty and has no function
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * init
     * Initializes the map if map services are working, and location permission is granted.
     */
    private void init() {
        Log.d(TAG, "init: mLocationPermissionGranted " + mLocationPermissionGranted);
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("flag","1");
                startActivity(intent);
                getmap = false;
            } else {
                getLocationPermission();
            }
        }

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
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            if(getmap){
                init();
            }
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

    /**
     * onRequestPermissionsResult
     * it looks at the result of the request to access location and makes changes accordingly
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //mLocationPermissionGranted = false;
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
                    if(getmap){
                        init();
                    }
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }
}