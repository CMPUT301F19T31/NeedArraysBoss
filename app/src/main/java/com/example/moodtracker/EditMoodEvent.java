package com.example.moodtracker;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * This is an activity that handles editing for existing moods
 */
public class EditMoodEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EmojiEditText et;
    private Spinner feelingSpinner, socialStateSpinner;
    private int index;      // holds the index of the event in the db that is being edited
    private String feeling = "", socialState = "";

    private FirebaseAuth mAuth;
    private DocumentReference docRef;
    private Mood mood;
    private User user;

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
        feelingSpinner.setOnItemSelectedListener(this);
        socialStateSpinner = findViewById(R.id.socialStateSpinner2);
        socialStateSpinner.setOnItemSelectedListener(this);
        index = getIntent().getExtras().getInt("index");
        initializeArrays();

        //load data from DB
        mAuth = FirebaseAuth.getInstance();
        signIn();

    }

    public void signIn() {
        mAuth.signInWithEmailAndPassword("ahnafon3@gmail.com", "123456")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("sample", "signInWithEmail:success");
                            docRef = FirebaseFirestore.getInstance().collection("users").document("user"+mAuth.getCurrentUser().getEmail());
                            loadDataFromDB();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("sample", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loadDataFromDB() {
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                mood = user.getMoodHistory().get(index);

                //initialise spinners and edittexts
                et.setText(mood.getReason());
                feelingSpinner.setSelection(moods.indexOf(mood.getFeeling() + 1));
                socialStateSpinner.setSelection(moods.indexOf(mood.getSocialState() + 1));
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
                user.getMoodHistory().set(index, mood);
                docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
            }

        } else if (feeling.equals(""))
            Toast.makeText(getApplicationContext(), "Please select how you feel", Toast.LENGTH_LONG).show();
    }

    public void deleteMood(View v) {
        user.getMoodHistory().remove(index);
        docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    public void cancel(View v) { finish(); }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.editMoodFeelingSpinner)
            feeling = parent.getItemAtPosition(position).toString();
        else if(parent.getId() == R.id.socialStateSpinner2)
            socialState = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}