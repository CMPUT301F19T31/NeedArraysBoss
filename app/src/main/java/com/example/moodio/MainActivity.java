package com.example.moodio;

import androidx.appcompat.app.AppCompatActivity;

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

    Dialog dialog;
    private ArrayList<Mood> moodHistory;
    private ArrayList<String> moodHistoryStr;
    private ArrayAdapter<String> moodHistoryAdapter;
    private String feeling = "", socialState = "";
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
        moodHistory = new ArrayList<Mood>();
        moodHistoryStr = new ArrayList<>();
        moodHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moodHistoryStr);
        lv = findViewById(R.id.moodList);
        lv.setAdapter(moodHistoryAdapter);
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
                EditText et = dialog.findViewById(R.id.reasonET);
                String reason = et.getText().toString();
                SimpleDateFormat datetime = new SimpleDateFormat("(yyyy/MM/dd) 'at' HH:mm");
                String datetimeStr = datetime.format(new Date());

                if(!feeling.equals("") && !socialState.equals("")) {
                    Mood newMood;

                    if (reason == null) {
                        newMood = new Mood(feeling, socialState, datetimeStr);
                    } else {
                        newMood = new Mood(feeling, socialState, datetimeStr, reason);
                    }
                    moodHistory.add(newMood);
                    moodHistoryStr.add(newMood.toString());

                    feeling = "";
                    socialState = "";
                    dialog.dismiss();   //closes the pop up window
                    moodHistoryAdapter.notifyDataSetChanged();

                } else if (feeling.equals("")) {
                    Toast.makeText(dialog.getContext(), "Please select how you feel", Toast.LENGTH_LONG).show();
                } else if (socialState.equals("")) {
                    Toast.makeText(dialog.getContext(), "Please select a social state", Toast.LENGTH_LONG).show();
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
