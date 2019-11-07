package com.example.moodio;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class EditMoodEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EmojiEditText et;
    private Spinner feelingSpinner, socialStateSpinner;
    private int index;      // holds the index of the event in the db that is being edited
    private String feeling = "", socialState = "";

    private FirebaseDatabase db;
    private DatabaseReference dataRef;
    private Mood mood;
    private ArrayList<Mood> moodHistory;

    private ArrayList<String> moods;
    private ArrayList<String> socialStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mood_event);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        et = findViewById(R.id.reasonET2);
        feelingSpinner = findViewById(R.id.editMoodFeelingSpinner);
        socialStateSpinner = findViewById(R.id.socialStateSpinner2);
        index = getIntent().getExtras().getInt("index");
        initializeArrays();

        //load data from DB
        db = FirebaseDatabase.getInstance();
        dataRef = db.getReference("moodEvents");
        loadDataFromDB();   // gets the mood clicked from the db

        //initialise spinners and edittexts
        et.setText(mood.getReason());
        feelingSpinner.setSelection(moods.indexOf(mood.getFeeling() + 1));
        socialStateSpinner.setSelection(moods.indexOf(mood.getSocialState() + 1));

    }

    public void addMoodToDB () {
        final DocumentReference docRef = db.document("/users/user_ahnav/user_events/moodEvents");
        moodHM.remove("event"+index);
        moodHM.put("event"+index, mood);

        docRef.set(moodHM).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("Sample", "Document save successful");
                } else {
                    Log.d("Sample", "Document save unsuccessful");
                }
            }
        });
    }

    public void loadDataFromDB() {
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                moodHistory = dataSnapshot.getValue(ArrayList.class);
                mood = moodHistory.get(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initializeArrays() {
        moods = new ArrayList<>();
        moods.add("happy");
        moods.add("excited");
        moods.add("hopeful");
        moods.add("satisfied");
        moods.add("sad");
        moods.add("angry");
        moods.add("frustrated");
        moods.add("confused");
        moods.add("annoyed");
        moods.add("hopeless");
        moods.add("lonely");

        socialStates = new ArrayList<>();
        socialStates.add("alone");
        socialStates.add("with one person");
        socialStates.add("with two or more people");
        socialStates.add("with a crowd");
    }

    public void editMoodEvent(View v) {
        boolean change = false;

        feelingSpinner.setOnItemSelectedListener(this);
        socialStateSpinner.setOnItemSelectedListener(this);
        String reason = et.getText().toString();

        if(!feeling.equals("")) {
            if(!feeling.equals(mood.getFeeling())) {
                change = true;
                mood.setFeeling(feeling);
            }
            if(!reason.equals(mood.getReason())) {
                change = true;
                mood.setReason(reason);
            }
            if(!socialState.equals(mood.getSocialState())) {
                change = true;
                mood.setSocialState(socialState);
            }

            if(change) {
                addMoodToDB();
                finish();
            }

        } else if (feeling.equals(""))
            Toast.makeText(getApplicationContext(), "Please select how you feel", Toast.LENGTH_LONG).show();
    }

    public void deleteMood(View v) {
        final DocumentReference docRef = db.document("/users/user_ahnav/user_events/moodEvents");
        moodHM.remove("event"+index);

        docRef.set(moodHM).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("Sample", "Document save successful");
                    finish();
                } else {
                    Log.d("Sample", "Document save unsuccessful");
                }
            }
        });
    }

    public void cancel(View v) { finish(); }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.feelingSpinner)
            feeling = parent.getItemAtPosition(position).toString();
        else if(parent.getId() == R.id.socialStateSpinner)
            socialState = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
