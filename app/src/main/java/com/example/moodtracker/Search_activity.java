package com.example.moodtracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

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
        list = new ArrayList<String>();

        mAuth = FirebaseAuth.getInstance();
        collectionReference = null;

        //list.add("hello");
        //list.add("good morning");

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
                        //Toast.makeText(getContext(),Integer.toString(data.size()),Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < data.size(); i++) {

                            //list.addAll(data);
                            list.add(data.get(i).toObject(User.class).getUserID());
                            //list.add(data.get(i).toObject(User.class));
                            //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }

                        //Toast.makeText(getActivity(), myList.get(0).getEmail(), Toast.LENGTH_SHORT).show();
                        //userAdapter.notifyDataSetChanged();
                    }
                });
            }
            adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
            myList.setAdapter(adapter);

            mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    adapter.getFilter().filter(s);
                    return false;
                }
            });
        }


}
