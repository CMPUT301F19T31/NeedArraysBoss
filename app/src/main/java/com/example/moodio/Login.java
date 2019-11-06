package com.example.moodio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button SignIn;
    TextView TextSignIn;
    FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        SignIn = findViewById(R.id.login);
        TextSignIn = findViewById(R.id.textView);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(Login.this, "Fields Are Empty!", Toast.LENGTH_SHORT);
                }
                else if(!(emailID.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(emailID, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Login.this, "Login Unsuccessful. Please try again!", Toast.LENGTH_SHORT);
                            }
                            else {
                                startActivity(new Intent(Login.this,MainActivity.class));
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(Login.this, "Error Ocurred!", Toast.LENGTH_SHORT);
                }
            }
        });

        TextSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,SignUpActivity.class);
                startActivity(i);


            }
        });
    }

}