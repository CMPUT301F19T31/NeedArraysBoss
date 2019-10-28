package com.example.moodio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontRequest;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.emoji.widget.EmojiEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Dialog dialog;
    private String feeling = "", socialState = "";

    private RecyclerView rv;
    private ArrayList<Mood> moodHistory;
    private RecyclerView.Adapter moodHistoryAdapter;
    private RecyclerView.LayoutManager moodHistoryLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up emoji compatibility
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
        moodHistory = new ArrayList<Mood>();
        rv = findViewById(R.id.moodList);
        moodHistoryLM = new LinearLayoutManager(this);
        moodHistoryAdapter = new MoodListAdapter(moodHistory);
        rv.setLayoutManager(moodHistoryLM);
        rv.setAdapter(moodHistoryAdapter);

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
