package com.example.moodtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;




public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DocumentReference docRef;
    private User user;
    private User user2;
    private Following following;
    private ImageView imageView;
    private TextView usernameTV;
    private TextView emailTV;
    private TextView followingTV;
    private TextView followerTV;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    DocumentReference docRef2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        imageView = root.findViewById(R.id.imgUser);
        usernameTV = root.findViewById(R.id.tv_uname);
        emailTV = root.findViewById(R.id.tv_address);
        followerTV = root.findViewById(R.id.pfollower);
        followingTV = root.findViewById(R.id.pfollowing);
        list = new ArrayList<String>();
        final ListView notificationLV = root.findViewById(R.id.notificationsLV);
        adapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,list);
        notificationLV.setAdapter(adapter);

        docRef = FirebaseFirestore.getInstance().collection("users").document("user"+mAuth.getCurrentUser().getEmail());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                loadDataFromDB();
            }
        });

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

        notificationLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Notification notification = user.getNotification().get((int) id);
                if(notification.getType()==1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Give user permission to follow?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            docRef2 = FirebaseFirestore.getInstance().collection("users").document("user"+notification.getUser1());
                            docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    following = new Following(1, user.getEmail());
                                    user2 = documentSnapshot.toObject(User.class);
                                    user2.getFollowingList().add(following);
                                    docRef2.set(user2);

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //no
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                user.getNotification().remove((int)id);
                user.setNumFollwers(user.getNumFollwers()+1);
                docRef.set(user);
                list.remove((int)id);
                adapter.notifyDataSetChanged();
            }
        });




        return root;
    }

    public void loadDataFromDB() {
        if(user == null) {
            return;
        }

        decodeImage(user.getProfilePic(), imageView);
        usernameTV.setText(user.getUserID());
        emailTV.setText(user.getEmail());
        followingTV.setText(Integer.toString(user.getFollowingList().size()));
        followerTV.setText(Integer.toString(user.getNumFollwers()));
        for (int i = 0; i < user.getNotification().size(); i++) {
            list.add(user.getNotification().get(i).getString());
        }

    }

    /**
     * This is a helper function that takes the ImageView and image as a string and sets
     * the image of the ImageView to the image
     * @param completeImageData the image file represented as a string
     * @param imageView the view on which the image will be shown
     */
    public void decodeImage(String completeImageData, ImageView imageView) {
        if (completeImageData == null) { return; }

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        imageView.setImageBitmap(bitmap);
    }
}