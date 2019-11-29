package com.example.moodtracker;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment that displays a list of mood events created by the user's following list
 */
public class FollowingMoods extends Fragment implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private User currentUser;
    private ArrayList<User> allUsers;
    private ArrayList<String> friends;

    private RecyclerView rv;
    private ArrayList<Mood> friendMoodHistory;
    private ArrayList<Mood> filterFriendMoodHistory;
    private MoodListAdapter friendMoodHistoryAdapter;
    private RecyclerView.LayoutManager rvLM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

                Intent intent = new Intent(getActivity().getApplicationContext(), ViewFriendMood.class);
                intent.putExtra("email", email);
                intent.putExtra("index", i);
                startActivity(intent);
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
        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                friendMoodHistory.clear();
                allUsers.clear();
                for(DocumentSnapshot doc: data) {
                    User user = doc.toObject(User.class);
                    if(friends.contains(user.getEmail())) { //if user is a friend
                        allUsers.add(user);
                        int permission = -1;
                        for(int i=0; i<currentUser.getFollowingList().size(); i++) {
                            if(currentUser.getFollowingList().get(i).getUser().equals(user.getEmail())) {
                                permission = currentUser.getFollowingList().get(i).getType();
                                break;
                            }
                        }
                        if(permission == 2) {    // if only most recent mood is allowed
                            Mood mood = user.getMoodHistory().get(0);
                            mood.setFriend(user.getUserID());
                            friendMoodHistory.add(mood);
                        }
                        else {                  // if all moods are allowed
                            for (int i = 0; i < user.getMoodHistory().size(); i++) {
                                Mood mood = user.getMoodHistory().get(i);
                                mood.setFriend(user.getUserID());
                                friendMoodHistory.add(mood);
                            }
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
}