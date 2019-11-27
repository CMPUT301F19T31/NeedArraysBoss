package com.example.moodtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class search_userdata extends AppCompatActivity {
    private TextView textView1, tv, tv_uid;
    private DocumentReference docRef;
    private FirebaseAuth mAuth;
    private String text;
    private User user;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_userdata);
        mAuth = FirebaseAuth.getInstance();

        final Button testButton = (Button) findViewById(R.id.button1);
        testButton.setTag(1);


        tv=findViewById(R.id.emailaddress);
        tv.setText(getIntent().getStringExtra("email"));

        text=getIntent().getStringExtra("email");


        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Notification notification = new Notification(1, mAuth.getCurrentUser().getEmail(),getIntent().getStringExtra("email"));
                documentReference = FirebaseFirestore.getInstance().collection("users").document(getIntent().getStringExtra("email"));
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        int flag=1;
                        for(int i=0; i<user.getNotification().size();i++)
                        {
                            if(user.getNotification().get(i).getUser1().compareTo(mAuth.getCurrentUser().getEmail())==0 && user.getNotification().get(i).getType()==1)
                            {
                                flag=0;
                                Toast.makeText(getApplicationContext(), "Follow Request Already Pending!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(flag==1) {
                            user.getNotification().add(notification);
                            documentReference.set(user);
                            Toast.makeText(getApplicationContext(), "Follow Request Sent!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            if(user.getFollowingList()==null || user.getFollowingList().isEmpty()) {
                textView1.setText("0");
            }
            else {
                textView1.setText(user.getFollowingList().size());
            }
            tv_uid=findViewById(R.id.tv_name);
            tv_uid.setText(user.getUserID());
        }
    });



    }
}




