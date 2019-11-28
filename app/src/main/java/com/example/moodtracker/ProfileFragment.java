package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moodtracker.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    String email;
    DocumentReference docRef;
    User user;
    TextView profile_name, address;
    ImageView profile_image;

    FirebaseAuth mAuth;
    EditText edit_profile_name;
    String temporary;
    Button save;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        Button logout = root.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        profile_name = root.findViewById(R.id.tv_name);
        profile_image = root.findViewById(R.id.image_profile);
        edit_profile_name = root.findViewById(R.id.tv_name_edit);
        edit_profile_name.setVisibility(View.INVISIBLE);
        address = root.findViewById(R.id.tv_address);
        save = root.findViewById(R.id.save);
        save.setVisibility(View.INVISIBLE);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        email = currentFirebaseUser.getEmail();


        docRef = FirebaseFirestore.getInstance().collection("users")
                .document("user"+email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);

                profile_name.setText(user.getUserID());
                address.setText(user.getEmail());
                temporary = user.getUserID();

            }
        });

        ImageView edit = root.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_profile_name.setText(temporary);
                profile_name.setVisibility(View.INVISIBLE);
                edit_profile_name.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setUserID(edit_profile_name.getText().toString());
                        getFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
                    }
                });
            }
        });


        return root;
    }
}

