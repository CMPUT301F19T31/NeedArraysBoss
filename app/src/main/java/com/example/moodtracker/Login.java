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

public class Login extends AppCompatActivity {
    EditText email, password;
    Button SignIn;
    TextView TextSignIn;
    FirebaseAuth mFirebaseAuth;
    String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        SignIn = findViewById(R.id.login);
        TextSignIn = findViewById(R.id.textView);

    }

    public void signInUser (View v) {
        String emailID = email.getText().toString();
        String pwd = password.getText().toString();
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
                            finish();
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

}