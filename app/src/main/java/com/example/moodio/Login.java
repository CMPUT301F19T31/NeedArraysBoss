package com.example.moodio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button SignIn;
    TextView TextSignIn;
    FirebaseAuth mFirebaseAuth;
    String TAG = "";
    FirebaseAuth.AuthStateListener mAuthlistener;



    /*@Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthlistener);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mFirebaseAuth = FirebaseAuth.getInstance();

        /*if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }*/

        setContentView(R.layout.login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        SignIn = findViewById(R.id.login);
        TextSignIn = findViewById(R.id.textView);

        TextSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,SignUpActivity.class);
                startActivity(i);


            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

        //final CollectionReference collectionReference = mFirebaseAuth.collection("Email");

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
                else {
                    mFirebaseAuth.signInWithEmailAndPassword(emailID,pwd)
                            .addOnSuccessListener(Login.this, new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent j = new Intent(Login.this, MainActivity.class);
                                    startActivity(j);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(Login.this, "Login Unsuccessful. Please try again!", Toast.LENGTH_SHORT);

                            }
                    });
                    /*(!(emailID.isEmpty() && pwd.isEmpty())){*/
//                    mFirebaseAuth.signInWithEmailAndPassword(emailID, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                //Log.d(TAG, "signInWithEmail:failure", task.getException());
//                                Toast.makeText(Login.this, "Login Unsuccessful. Please try again!", Toast.LENGTH_SHORT);
//                            }
//                            else {
//                                //Log.d(TAG, "signInWithEmail:success");
//                                //FirebaseUser user = mFirebaseAuth.getCurrentUser();
//                                Intent j = new Intent(Login.this, MainActivity.class);
//                                startActivity(j);
//                                finish();
//                            }
//                        }
//                    });
                }
            }
        });

        /*mAuthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(Login.this, MainActivity.class));
                }
            }
        };*/



    }

}