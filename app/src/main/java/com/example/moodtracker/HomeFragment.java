package com.example.moodtracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Dialog dialog;
    private String feeling = "", socialState = "";

    private RecyclerView rv;
    private ArrayList<Mood> moodHistory;
    private MoodListAdapter moodHistoryAdapter;
    private RecyclerView.LayoutManager moodHistoryLM;

    private FirebaseDatabase db;
    private DatabaseReference moodEventsDR;
    private FirebaseAuth mAuth;
    private static final String TAG = "sample";
    private User user;
    private FloatingActionButton actn_btn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        //textView.setText(s);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(getContext());
        EmojiCompat.init(config);

        dialog = new Dialog(getContext());
        moodHistory = new ArrayList<Mood>();
        actn_btn = root.findViewById(R.id.addMoodEvent);

        actn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMoodEvent(v);
            }
        });

        //initialize recyclerview
        rv = root.findViewById(R.id.moodList);
        moodHistoryLM = new LinearLayoutManager(getContext());
        moodHistoryAdapter = new MoodListAdapter(moodHistory);
        rv.setLayoutManager(moodHistoryLM);
        rv.setAdapter(moodHistoryAdapter);

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
        db = FirebaseDatabase.getInstance();
        moodEventsDR = null;

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) { // not signed in
            //start login/register activity

            //temp code (start)
            signInUser();
            //temp code (end)
        } else {
            moodEventsDR = db.getReference("moodEvents");
            loadDataFromDB();
        }
    }

    public void createUser() {
        mAuth.createUserWithEmailAndPassword("ahnafon3@gmail.com", "123456")
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            signInUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity().getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signInUser() {
        mAuth.signInWithEmailAndPassword("ahnafon3@gmail.com", "123456")
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            moodEventsDR = db.getReference("moodEvents");
                            loadDataFromDB();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext().getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromDB();
    }

    public void loadDataFromDB() {
        if(moodEventsDR == null) { return; }

        moodEventsDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Mood>> temp = new GenericTypeIndicator<ArrayList<Mood>>() {};
                ArrayList<Mood> tempList = dataSnapshot.getValue(temp);
                if (tempList != null) {
                    moodHistory.clear();
                    for(int i=0;i<tempList.size();i++)
                    {
                        moodHistory.add(tempList.get(i));
                    }
                    moodHistoryAdapter.notifyDataSetChanged();
                    //moodHistory = new ArrayList<>(tempList);
                    //moodHistoryAdapter = new MoodListAdapter(moodHistory);
                    //rv.setAdapter(moodHistoryAdapter);
                    Log.d(TAG, "Read detected. Read successful");


                } else {
                    moodEventsDR.setValue(moodHistory);
                    Log.d(TAG, "Read detected. moodEvents not found. File created.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void createMoodEvent(View view) {
        dialog.setContentView(R.layout.add_mood_event); //opens the pop window

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
                    moodEventsDR.setValue(moodHistory);

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
}