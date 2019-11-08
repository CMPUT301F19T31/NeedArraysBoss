package com.example.moodtracker;

import android.os.Bundle;
import android.view.View;
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

public class SignUpActivity extends AppCompatActivity {
    EditText username, password, repassword, email, phone;
    Button SignUp;
    TextView TextSignUp;
    FirebaseAuth mFirebaseAuth;
    String TAG = "";
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

    }

    public void signUpUser(View v) {
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
            Toast.makeText(SignUpActivity.this, "Fields Are Empty!", Toast.LENGTH_LONG);
        }
        else if(!(emailID.isEmpty() && pwd.isEmpty() && uname.isEmpty() && repwd.isEmpty() && pno.isEmpty())){
            mFirebaseAuth.createUserWithEmailAndPassword(emailID, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){

                        Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful. Please try again!", Toast.LENGTH_LONG);
                    }
                    else {
                        finish();
                    }
                }
            });
        }
        else {
            Toast.makeText(SignUpActivity.this, "Error Ocurred!", Toast.LENGTH_SHORT);
        }
    }

    public void cancel(View v) { finish(); }
}
