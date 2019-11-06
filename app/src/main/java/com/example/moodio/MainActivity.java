package com.example.moodio;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Dialog dialog;
    private String feeling = "", socialState = "";

    private RecyclerView rv;
    private ArrayList<Mood> moodHistory;
    private MoodListAdapter moodHistoryAdapter;
    private RecyclerView.LayoutManager moodHistoryLM;
    private HashMap<String, Mood> moodHistoryHM;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
        moodHistory = new ArrayList<Mood>();

        //initialize recyclerview
        rv = findViewById(R.id.moodList);
        moodHistoryLM = new LinearLayoutManager(this);
        moodHistoryAdapter = new MoodListAdapter(moodHistory);
        rv.setLayoutManager(moodHistoryLM);
        rv.setAdapter(moodHistoryAdapter);

        moodHistoryAdapter.setOnClickListener(new MoodListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Intent intent = new Intent(getApplicationContext(), EditMoodEvent.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });

        //initialize firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        moodHistoryHM = new HashMap<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) { // not signed in
            //start login/register activity

            //temp code (start)
            
            //temp code (end)
        }
        loadDataFromDB();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromDB();
    }

    public void loadDataFromDB() {
        db.document("/users/user_ahnav/user_events/moodEvents").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Mood mood;
                for(int i = 1; i <= documentSnapshot.getData().size(); i++) {
                    mood = (Mood) documentSnapshot.getData().get("event" + i);
                    moodHistoryHM.put("event" + i, mood);
                    moodHistory.add(mood);
                }
                moodHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addEventToDB(Mood newMood) {
        final DocumentReference docRef = db.document("/users/user_ahnav/user_events/moodEvents");

        moodHistoryHM.put("event" + moodHistory.size(), newMood);

        docRef.set(moodHistoryHM).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "Document save successful");
                } else {
                    Log.d(TAG, "Document save unsuccessful");
                }
            }
        });

        final DocumentReference docRef2 = db.document("/users/user_ahnav/user_events/totalEvents");
        Map<String, Integer> data2 = new HashMap<>();
        data2.put("size", moodHistory.size());
        docRef2.set(data2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "Size save successful");
                } else {
                    Log.d(TAG, "Size save unsuccessful");
                }
            }
        });
    }

    public void createMoodEvent(View view) {
        dialog.setContentView(R.layout.addmoodevent); //opens the pop window

        Spinner feelingSpinner = (Spinner) dialog.findViewById(R.id.feelingSpinner);
        feelingSpinner.setOnItemSelectedListener(this);

        Spinner socialStateSpinner = (Spinner) dialog.findViewById(R.id.socialStateSpinner);
        socialStateSpinner.setOnItemSelectedListener(this);

        Button addEventBtn = dialog.findViewById(R.id.addMoodEvent);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmojiEditText et = dialog.findViewById(R.id.reasonET);
                String reason = et.getText().toString();
                SimpleDateFormat datetime = new SimpleDateFormat("(yyyy/MM/dd) 'at' HH:mm");
                String datetimeStr = datetime.format(new Date());

                if(!feeling.equals("")) {
                    Mood newMood;

                    if (reason == null) {
                        newMood = new Mood(feeling, socialState, datetimeStr);
                    } else {
                        newMood = new Mood(feeling, socialState, datetimeStr, reason);
                    }
                    moodHistory.add(0, newMood); //inserts new mood at the beginning of list
                    addEventToDB(newMood);

                    feeling = "";
                    socialState = "";
                    dialog.dismiss();   //closes the pop up window
                    moodHistoryAdapter.notifyDataSetChanged();

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
}
