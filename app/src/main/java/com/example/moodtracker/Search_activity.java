package com.example.moodtracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Search_activity extends AppCompatActivity {

    SearchView mySearchView;
    ListView myList;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_users);

        mySearchView = (SearchView) findViewById(R.id.searchView);
        myList = (ListView) findViewById((R.id.myList));

        mAuth = FirebaseAuth.getInstance();
        collectionReference = null;

    }


        @Override
        public void onStart() {
            super.onStart();
            if (mAuth.getCurrentUser() != null) {
                collectionReference = FirebaseFirestore.getInstance().collection("users");
                collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                        ///Toast.makeText(getContext(),Integer.toString(data.size()),Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < data.size(); i++) {

                            list.addAll(data);
                            //list.add(data.get(i).toObject(User.class));
                            //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(getActivity(), myList.get(0).getEmail(), Toast.LENGTH_SHORT).show();
                        //userAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

}
