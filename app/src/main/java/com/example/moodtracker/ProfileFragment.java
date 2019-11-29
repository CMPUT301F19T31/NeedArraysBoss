package com.example.moodtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DocumentReference docRef;
    private User user;
    private User user2;
    private Following following;
    private TextView usernameTV, emailTV;
    private ImageView imageView;
    private TextView followingTV;
    private TextView followerTV;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    DocumentReference docRef2;
    
    private Dialog dialog;
    private EditText editUsername;
    private ImageView editImage;
    private ArrayList<String> listOfUsers;
    private String profilePic;
    private boolean profilePicChanged = false;

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
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(mAuth.getCurrentUser()==null)
                    return;
                user = documentSnapshot.toObject(User.class);
                loadDataFromDB();
                adapter.notifyDataSetChanged();
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
                    getActivity().finish();
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
                final int fid = (int) id;
                if(notification.getType()==1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Give user permission to follow?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                            builder1.setMessage("Allow user to follow all moods?");
                            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    docRef2 = FirebaseFirestore.getInstance().collection("users").document("user"+notification.getUser1());
                                    docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            following = new Following(1, user.getEmail());
                                            user2 = documentSnapshot.toObject(User.class);
                                            user2.getFollowingList().add(following);
                                            Notification notification2 = new Notification(2, user.getEmail(), user2.getEmail());
                                            user2.getNotification().add(notification2);
                                            docRef2.set(user2);
                                            user.setNumFollwers(user.getNumFollwers()+1);
                                            user.getNotification().remove(fid);
                                            docRef.set(user);

                                        }
                                    });
                                }
                            });
                            builder1.setNeutralButton("Only Recent Moods", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    docRef2 = FirebaseFirestore.getInstance().collection("users").document("user"+notification.getUser1());
                                    docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            following = new Following(2, user.getEmail());
                                            user2 = documentSnapshot.toObject(User.class);
                                            user2.getFollowingList().add(following);
                                            Notification notification2 = new Notification(2, user.getEmail(), user2.getEmail());
                                            user2.getNotification().add(notification2);
                                            docRef2.set(user2);
                                            user.setNumFollwers(user.getNumFollwers()+1);
                                            user.getNotification().remove(fid);
                                            docRef.set(user);

                                        }
                                    });
                                }
                            });
                            AlertDialog dialog1 = builder1.create();
                            dialog1.show();

                        }
                    });
                    builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            docRef2 = FirebaseFirestore.getInstance().collection("users").document("user"+notification.getUser1());
                            docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    user2 = documentSnapshot.toObject(User.class);
                                    Notification notification2 = new Notification(3, user.getEmail(), user2.getEmail());
                                    user2.getNotification().add(notification2);
                                    docRef2.set(user2);
                                    user.getNotification().remove(fid);
                                    docRef.set(user);

                                }
                            });
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else if(notification.getType()==2)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Clearing Notification");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.getNotification().remove(fid);
                            docRef.set(user);

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else if(notification.getType()==3)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Clearing Notification");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.getNotification().remove(fid);
                            docRef.set(user);

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else if(notification.getType()==4)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Clearing Notification");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.getNotification().remove(fid);
                            docRef.set(user);

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                list.remove((int)id);
                adapter.notifyDataSetChanged();
            }
        });

      
        ImageView edit = root.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        return root;
    }

    /**
     * loadDataFromDB
     * Uses data from database to fill in the fields in the layout.
     */
    public void loadDataFromDB() {
        if(user == null) {
            return;
        }
        decodeImage(user.getProfilePic(), imageView);
        usernameTV.setText(user.getUserID());
        emailTV.setText(user.getEmail());
        followingTV.setText(Integer.toString(user.getFollowingList().size()));
        followerTV.setText(Integer.toString(user.getNumFollwers()));
        list.clear();
        for (int i = 0; i < user.getNotification().size(); i++) {
            list.add(user.getNotification().get(i).getString());
        }

        getUsers();
    }

    /**
     * editProfile
     * Is called when the user presses the edit button. It handles the dialog window that allows
     * the user to change their profile picture and username.
     */
    public void editProfile() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.edit_profile);

        editUsername = dialog.findViewById(R.id.tv_uname);
        TextView editEmail = dialog.findViewById(R.id.tv_address);
        editImage = dialog.findViewById(R.id.imgUser);

        editUsername.setText(user.getUserID());
        editEmail.setText(user.getEmail());
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Email cannot be changed.", Toast.LENGTH_SHORT).show();
            }
        });
        decodeImage(user.getProfilePic(), editImage);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
            }
        });

        TextView save = dialog.findViewById(R.id.save_edit);
        save.setOnClickListener(new View.OnClickListener() {    // save changes
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                if(username.equals("")) {
                    Toast.makeText(getContext(), "Username cannot be null", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!checkUsername(username)) {
                    Toast.makeText(getContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setUserID(username);
                if(profilePicChanged) {
                    user.setProfilePic(profilePic);
                    profilePic = null;
                    profilePicChanged = false;
                }
                docRef.set(user);
                dialog.dismiss();
                loadDataFromDB();
            }
        });

        TextView cancel = dialog.findViewById(R.id.cancel_edit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();   // cancel changes
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * getUsers
     * This is a helper function to loadDataFromDB. It retrieves all the users from the database.
     */
    public void getUsers() {
        CollectionReference ref = FirebaseFirestore.getInstance().collection("users");
        listOfUsers = new ArrayList<>();

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot doc: task.getResult()) {
                        listOfUsers.add(doc.toObject(User.class).getUserID());
                    }
                    listOfUsers.remove(user.getUserID());
                }
            }
        });
    }

    /**
     * checkUsername
     * This function checks if the username entered already exists
     * @return true if doesnt exist, else false
     */
    public boolean checkUsername(String username) {
        for(int i=0; i<listOfUsers.size(); i++) {
            if(listOfUsers.get(i).compareTo(username)==0)
                return false;
        }
        return true;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 3: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri image = data.getData();
                    try {
                        Bitmap temp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        temp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        int num = 50;
                        while (byteArray.length > 10000 && num > 0) {   // compress image to not more than 10 kb
                            byteArrayOutputStream.flush();
                            byteArrayOutputStream.reset();

                            temp.compress(Bitmap.CompressFormat.JPEG, num, byteArrayOutputStream);
                            num = num / 2;
                            byteArray = byteArrayOutputStream.toByteArray();
                        }
                        this.profilePic = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        profilePicChanged = true;
                        Toast.makeText(dialog.getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                        decodeImage(profilePic, editImage);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}