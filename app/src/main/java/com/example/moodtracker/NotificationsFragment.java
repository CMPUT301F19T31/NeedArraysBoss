package com.example.moodtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private User currentUser;
    private ArrayList<String> friends;

    private RecyclerView rv;
    private ArrayList<Mood> friendMoodHistory;
    private MoodListAdapter friendMoodHistoryAdapter;
    private RecyclerView.LayoutManager rvLM;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(getContext());
        EmojiCompat.init(config);

        // initialise variables
        mAuth = FirebaseAuth.getInstance();

        friends = new ArrayList<>();
        rv = root.findViewById(R.id.FollowingMoodlist);
        friendMoodHistory = new ArrayList<>();
        friendMoodHistoryAdapter = new MoodListAdapter(friendMoodHistory);
        rvLM = new LinearLayoutManager(getContext());
        rv.setAdapter(friendMoodHistoryAdapter);
        rv.setLayoutManager(rvLM);

        getFriendList();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) { return; }




    }

    public void getFriendList() {
        FirebaseFirestore.getInstance().collection("users")
                .document("user"+mAuth.getCurrentUser().getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                for(int i=0; i<currentUser.getFriendList().size(); i++)
                    friends.add(currentUser.getFriendList().get(i));
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
                    if(friends.contains(user.getUserID())) {
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
     */
    public void sortFriendList() {

    }
}