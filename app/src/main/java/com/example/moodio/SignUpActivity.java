package com.example.moodio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    EditText username, password, repassword, email, phone;
    Button SignUp;
    TextView TextSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        email = findViewById(R.id.signupemail);
        password = findViewById(R.id.signuppassword);
        repassword = findViewById(R.id.confirmpword);
        phone = findViewById(R.id.phone);
        SignUp = findViewById(R.id.signup);
        TextSignUp = findViewById(R.id.textView2);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    Toast.makeText(SignUpActivity.this, "You are Signed Up",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(i);

                }
                else {
                    Toast.makeText(SignUpActivity.this, "Please Sign Up",Toast.LENGTH_SHORT).show();
                }
            }
        };

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String emailID = email.getText().toString();
                String pwd = password.getText().toString();
                String repwd = repassword.getText().toString();
                String pno = phone.getText().toString();
                if (uname.isEmpty()) {
                    password.setError("Please enter your username");
                    password.requestFocus();
                }
                else if(emailID.isEmpty()){
                    email.setError("Please enter email");
                    email.requestFocus();
                }
                else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if (repwd.isEmpty()) {
                    password.setError("Please re-enter your password ");
                    password.requestFocus();
                }
                else if (pno.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if (emailID.isEmpty() && pwd.isEmpty() && uname.isEmpty() && repwd.isEmpty() && pno.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT);
                }
                else if(!(emailID.isEmpty() && pwd.isEmpty() && uname.isEmpty() && repwd.isEmpty() && pno.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(emailID, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful. Please try again!", Toast.LENGTH_SHORT);
                            }
                            else {
                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Error Ocurred!", Toast.LENGTH_SHORT);
                }
            }
        });
        TextSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(SignUpActivity.this, Login.class);
                startActivity(signup);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
