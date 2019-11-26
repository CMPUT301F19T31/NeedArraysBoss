package com.example.moodtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class search_userdata extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_userdata);


        final Button testButton = (Button) findViewById(R.id.button1);
        testButton.setTag(1);
        testButton.setText("Play");
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {

                    testButton.setText("Follow");
                    v.setTag(0); //pause
                } else {
                    testButton.setText("Request sent");
                    v.setTag(1); //pause
                }
            }
        });
    }
}