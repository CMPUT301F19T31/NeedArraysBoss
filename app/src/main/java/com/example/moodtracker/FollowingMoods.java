package com.example.moodtracker;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingMoods extends Fragment {

    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private User currentUser;
    private ArrayList<String> friends;

    private RecyclerView rv;
    private ArrayList<Mood> friendMoodHistory;
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

        // initialise variables
        mAuth = FirebaseAuth.getInstance();

        friends = new ArrayList<>();
        rv = root.findViewById(R.id.moodList);
        friendMoodHistory = new ArrayList<>();
        friendMoodHistoryAdapter = new MoodListAdapter(friendMoodHistory);
        rvLM = new LinearLayoutManager(getContext());
        rv.setAdapter(friendMoodHistoryAdapter);
        rv.setLayoutManager(rvLM);

        getFriendList();

        return root;
    }

    /**
     * Helper function to onCreateView. It connects to the database and retrieves the list
     * of users present in the database that the current user is friends with.
     */
    public void getFriendList() {
        FirebaseFirestore.getInstance().collection("users")
                .document("user"+mAuth.getCurrentUser().getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);

                for(int i=0; i<currentUser.getFollowingList().size(); i++)
                    friends.add(currentUser.getFollowingList().get(i).getUser());
                refreshList();
            }
        });
    }

    public void refreshList() {
        userRef = FirebaseFirestore.getInstance().collection("users");
        userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {     // get the users
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
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
}