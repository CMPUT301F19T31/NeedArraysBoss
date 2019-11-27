package com.example.moodtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static java.sql.Types.NULL;

public class search_userdata extends AppCompatActivity {
    private TextView textView1, tv, tv_uid;
    private DocumentReference docRef;
    private FirebaseAuth mAuth;
    private String text;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_userdata);


        final Button testButton = (Button) findViewById(R.id.button1);
        testButton.setTag(1);


        tv=findViewById(R.id.emailaddress);
        tv.setText(getIntent().getStringExtra("email"));

        text=getIntent().getStringExtra("email");


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

    /*@Override
    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {

     */
    docRef = FirebaseFirestore.getInstance().collection("users").document("user"+text);
    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            user = documentSnapshot.toObject(User.class);
            textView1=findViewById(R.id.following_no);

            if(user.getFriendList()==null || user.getFriendList().isEmpty()) {
                textView1.setText("0");
            }
            else {
                textView1.setText(user.getFriendList().size());
            }
            tv_uid=findViewById(R.id.tv_name);
            tv_uid.setText(user.getUserID());
        }
    });



    }
}




