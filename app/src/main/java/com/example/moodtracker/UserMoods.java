package com.example.moodtracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class UserMoods extends Fragment implements AdapterView.OnItemSelectedListener {

    private Dialog dialog;
    private String feeling = "", socialState = "";

    private RecyclerView rv;
    private ArrayList<Mood> moodHistory;
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
        button_search = root.findViewById(R.id.search);
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


    public void onResume() {
        super.onResume();
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
        if (user == null || user.getMoodHistory() == null) {
            return;
        }
        int size = user.getMoodHistory().size();

        moodHistory.clear();
        for (int i = 0; i < size; i++) {
            moodHistory.add(user.getMoodHistory().get(i));
        }
        moodHistoryAdapter.notifyDataSetChanged();
    }


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

        Button addEventBtn = dialog.findViewById(R.id.addMoodEvent);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmojiEditText et = dialog.findViewById(R.id.reasonET);
                String reason = et.getText().toString();

                if(!feeling.equals("")) {
                    Mood newMood;

                    if (reason == null && image == null) {
                        newMood = new Mood(feeling, socialState, System.currentTimeMillis());
                    } else if(image == null) {
                        newMood = new Mood(feeling, socialState, System.currentTimeMillis(), reason);
                    } else {
                        newMood = new Mood(feeling, socialState, System.currentTimeMillis(), reason, image);
                    }
                    moodHistory.add(0, newMood); //inserts new mood at the beginning of list
                    moodHistoryAdapter.notifyDataSetChanged();
                    user.setMoodHistory(moodHistory);
                    userRef.set(user);  // save to db

                    feeling = "";
                    socialState = "";
                    image = null;
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

        TextView imageTV = dialog.findViewById(R.id.imageTV);
        imageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
            }
        });

        dialog.show();  //opens the pop window
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.feelingSpinner)
            feeling = adapterView.getItemAtPosition(i).toString();
        else if (adapterView.getId() == R.id.socialStateSpinner)
            socialState = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");

        switch (requestCode) {
            case 3: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri image = data.getData();
                    try {
                        Bitmap temp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        temp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        this.image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
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

