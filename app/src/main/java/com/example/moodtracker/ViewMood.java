package com.example.moodtracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.emoji.widget.EmojiTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class ViewMood extends Fragment {

    private int index;
    private Mood mood;

    private TextView header, socialState;
    private EmojiTextView feeling, reason;
    private ImageView image;

    private HashMap<String, String> moodEmojis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_mood, container, false);

        //initialise the widgets
        header = root.findViewById(R.id.editMoodTitle);
        feeling = root.findViewById(R.id.feelingTV2);
        reason = root.findViewById(R.id.reasonTV2);
        socialState = root.findViewById(R.id.socialStateSpinner2);
        image = root.findViewById(R.id.moodImage);

        initArrays();

        String email = getActivity().getIntent().getExtras().getString("friend");
        index = getActivity().getIntent().getExtras().getInt("index");

        FirebaseFirestore.getInstance().collection("users").document("user"+email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mood = documentSnapshot.toObject(User.class).getMoodHistory().get(index);

                //load data into layout

            }
        });

        return root;
    }

    /**
     * Initializes the arrays for feelings and social states
     */
    public void initArrays() {

        //initializes emoji array
        moodEmojis = new HashMap<>();
        moodEmojis.put("happy", new String(Character.toChars(0x1F601)));
        moodEmojis.put("excited", new String(Character.toChars(0x1F606)));
        moodEmojis.put("hopeful", new String(Character.toChars(0x1F60A)));
        moodEmojis.put("satisfied", new String(Character.toChars(0x1F60C)));
        moodEmojis.put("sad", new String(Character.toChars(0x1F61E)));
        moodEmojis.put("angry", new String(Character.toChars(0x1F621)));
        moodEmojis.put("frustrated", new String(Character.toChars(0x1F623)));
        moodEmojis.put("confused", new String(Character.toChars(0x1F635)));
        moodEmojis.put("annoyed", new String(Character.toChars(0x1F620)));
        moodEmojis.put("hopeless", new String(Character.toChars(0x1F625)));
        moodEmojis.put("lonely", new String(Character.toChars(0x1F614)));
    }
}
