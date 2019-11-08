package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button SignIn;
    TextView TextSignIn;
    FirebaseAuth mFirebaseAuth;
    User user;
    String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        SignIn = findViewById(R.id.loginBtn);
        TextSignIn = findViewById(R.id.textView);

    }


    public void signInUser (View v) {
        final String emailID = email.getText().toString();
        final String pwd = password.getText().toString();
        if(emailID.isEmpty()){
            email.setError("Please enter email");
            email.requestFocus();
        }
        else if (pwd.isEmpty()){
            password.setError("Please enter your password");
            password.requestFocus();
        }
        else if (emailID.isEmpty() && pwd.isEmpty()){
            Toast.makeText(Login.this, "Fields Are Empty!", Toast.LENGTH_LONG);
        }
        else {
            mFirebaseAuth.signInWithEmailAndPassword(emailID,pwd)
                    .addOnSuccessListener(Login.this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            commitUser();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Login.this, "Login Unsuccessful. Please try again!", Toast.LENGTH_SHORT);
                }
            });

        }
    }

    public void createUser(View v) {
        Intent i = new Intent(Login.this,SignUpActivity.class);
        startActivity(i);
    }

    public void commitUser() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child("user" + mFirebaseAuth.getCurrentUser().getEmail().replace(".", "*"));
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user == null)
                    addUserToDB();
                else
                    finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addUserToDB() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users");
        String email = mFirebaseAuth.getCurrentUser().getEmail();
        user = new User("0", email,"000000");
        dataRef.child("user" + email.replace('.', '*')).setValue(user);
        finish();
    }

}