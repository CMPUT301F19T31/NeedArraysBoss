package com.example.moodio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Dialog dialog;
    ArrayList<Mood> moodHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
    }

    public void createMoodEvent(View view) {
        dialog.setContentView(R.layout.addmoodevent);

        String[] feelingArray = {"", "happy", "excited", "hopeful", "satisfied", "sad", "angry", "frustrated", "confused", "hopeless", "lonely"};
        String[] socialStateArray = {"", "alone", "with one person", "with two or more people", "with a crowd"};

        Spinner spinner = (Spinner) dialog.findViewById(R.id.feelingSpinner);
        ArrayAdapter<String> feelingSpinner  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, feelingArray);
        feelingSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(feelingSpinner);

        spinner = (Spinner) dialog.findViewById(R.id.socialStateSpinner);
        ArrayAdapter<String> socialStateSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, socialStateArray);
        socialStateSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(socialStateSpinner);

        Button addEventBtn = findViewById(R.id.addEventBtn);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = dialog.findViewById(R.id.reasonET);
                String reason = et.getText().toString();

                Mood newMood = new Mood(reason);

                moodHistory.add(newMood);
                dialog.dismiss();
            }
        });

        Button clearEventBtn = findViewById(R.id.clearMoodEvent);
        clearEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
