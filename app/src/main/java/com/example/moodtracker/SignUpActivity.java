package com.example.moodtracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    EditText username, password, repassword, email;
    String uname, emailID, pwd;
    Button SignUp;
    SignInButton GoogleSign;
    TextView TextSignUp;
    FirebaseAuth mFirebaseAuth;
    String TAG = "Error";
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ArrayList<User> users;
    GoogleSignInClient mGoogleSignInClient;
    //int RC_SIGN_IN = 0;
    private FirebaseAuth mAuth;
    private int permissions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        email = findViewById(R.id.signupemail);
        password = findViewById(R.id.signuppassword);
        repassword = findViewById(R.id.confirmpword);
        SignUp = findViewById(R.id.signup);
        TextSignUp = findViewById(R.id.textView2);

        GoogleSign = findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("160821997145-dhq5opf0nfn16qlq1s90fsr6ajd68mm7.apps.googleusercontent.com").requestEmail().build();
        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else{
                    Log.w("AUTH", "Account is NULL");
                    Toast.makeText(this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
                }
            } catch (ApiException e) {
                Log.w("AUTH", "Google sign in failed", e);
                Toast.makeText(this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("AUTH", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        emailID = acct.getEmail();
        pwd = acct.getId();
        uname = acct.getDisplayName();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("AUTH", "signInWithCredential:success");
                            if(mAuth.getCurrentUser() != null) {
                                commitUser();
                            }
                            mAuth.createUserWithEmailAndPassword(emailID,pwd ).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful.\n Please change your email try again!", Toast.LENGTH_LONG);
                                    } else {
                                        signInUser();
                                    }
                                }
                            });
                            //startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        } else {
                            Log.w("AUTH", "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == 0) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            // Signed in successfully, show authenticated UI.
//            uname = account.getDisplayName();
//            emailID = account.getEmail();
//            pwd = account.getId();
//
//
//            if(mFirebaseAuth.getCurrentUser() != null) {
//                commitUser();
//            }
//
//            mFirebaseAuth.createUserWithEmailAndPassword(emailID, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if(!task.isSuccessful()){
//                        Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful.\n Please change your email try again!", Toast.LENGTH_LONG);
//                    } else {
//                        signInUser();
//                    }
//                }
//            });
//
//           *//* GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//            if (acct != null) {
//                uname = acct.getDisplayName();
//                emailID = acct.getEmail();
//                pwd = acct.getId();
//
//            }*//*
//
//
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//        }
//    }*/

    public void signUpUser(View v) {
        uname = username.getText().toString();
        emailID = email.getText().toString();
        pwd = password.getText().toString();
        String repwd = repassword.getText().toString();

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

        else if (emailID.isEmpty() && pwd.isEmpty() && uname.isEmpty() && repwd.isEmpty()){
            Toast.makeText(SignUpActivity.this, "Fields Are Empty!", Toast.LENGTH_LONG);
        }
        else if(!(emailID.isEmpty() && pwd.isEmpty() && uname.isEmpty() && repwd.isEmpty())){
            if(mFirebaseAuth.getCurrentUser() != null) {
                commitUser();
            }

            mFirebaseAuth.createUserWithEmailAndPassword(emailID, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful.\n Please change your email try again!", Toast.LENGTH_LONG);
                    } else {
                        signInUser();
                    }
                }
            });
        }
        else {
            Toast.makeText(SignUpActivity.this, "Error Ocurred!", Toast.LENGTH_SHORT);
        }
    }

    public void signInUser() {
        mFirebaseAuth.signInWithEmailAndPassword(emailID,pwd)
                .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SignUpActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        commitUser();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Login Unsuccessful. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * commitUser
     * checks for requirments and adds user to database. This method returns the app to the
     * mainactivity if successful.
     */
    public void commitUser() {
        if(!checkUsername()) {
            Toast.makeText(SignUpActivity.this, "SignUpUnsuccessful. Username already exits!", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document("user"+mFirebaseAuth.getCurrentUser().getEmail());
        User user = new User(uname, emailID,pwd);

        userRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * getUsers
     * This is a helper function to checkUsername. It retrieves all the users from the database
     */
    public void getUsers() {
        CollectionReference ref = FirebaseFirestore.getInstance().collection("users");
        users = new ArrayList<>();
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot doc: task.getResult())
                        users.add(doc.toObject(User.class));
                }
            }
        });
    }

    /**
     * checkUsername
     * This function checks if the username entered already exists
     * @return true if doesnt exist, else false
     */
    public boolean checkUsername() {
        if(users == null)
            getUsers();

        for(int i=0; i<users.size(); i++) {
            if(users.get(i).getUserID().equals(uname))
                return false;
        }
        return true;
    }

    public void cancel(View v) { finish(); }
}
