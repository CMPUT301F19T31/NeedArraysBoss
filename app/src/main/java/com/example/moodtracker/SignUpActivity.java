package com.example.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    EditText username, password, repassword, email;
    String uname, emailID, pwd, image;
    Button SignUp;
    SignInButton GoogleSign;
    TextView TextSignUp;
    FirebaseAuth mFirebaseAuth;
    String TAG = "";
    GoogleSignInClient mGoogleSignInClient;
    private int permissions = 0;
    ImageView picture;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ArrayList<User> users;
    private boolean done = false;

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

        picture = findViewById(R.id.image_profile);

        GoogleSign = findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("160821997145-dhq5opf0nfn16qlq1s90fsr6ajd68mm7.apps.googleusercontent.com").requestEmail().build();

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

    public void uploadDP (View v) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 3: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri image = data.getData();
                    try {
                        Bitmap temp = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                        //picture.setImageBitmap(temp);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        temp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        this.image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            case 0: {
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
                    e.printStackTrace();
                    Log.w("AUTH", "Google sign in failed", e);
                    Toast.makeText(this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
                }
            }

        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("AUTH", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        emailID = acct.getEmail();
        uname = acct.getDisplayName();
        pwd = acct.getId();

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("AUTH", "signInWithCredential:success");
                            FirebaseUser you = mFirebaseAuth.getCurrentUser();
                            if(you != null)
                                getUsers();
                            //startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        } else {
                            commitUser();

                        }
                    }
                });
    }

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
            if(mFirebaseAuth.getCurrentUser() != null && users != null) {
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
                        getUsers();
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
        //checks if username is unique
        if(!checkUsername()) {
            Toast.makeText(SignUpActivity.this, "Login unsuccessful! Username already exists.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document("user"+mFirebaseAuth.getCurrentUser().getEmail());
        User user;
        if(image == null) {
            user = new User(uname, emailID, pwd);
        } else {
            user = new User(uname, emailID, pwd, image);
        }

        userRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    image = null;
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
                    for(DocumentSnapshot doc: task.getResult()) {
                        users.add(doc.toObject(User.class));
                    }
                    commitUser();
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
        for(int i=0; i<users.size(); i++) {
            if(users.get(i).getUserID().compareTo(uname)==0)
                return false;
        }
        return true;
    }

    public void cancel(View v) { finish(); }
}
