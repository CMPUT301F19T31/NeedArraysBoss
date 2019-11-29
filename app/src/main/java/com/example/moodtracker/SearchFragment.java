package com.example.moodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    SearchView mySearchView;
    ListView myList;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;

    ArrayList<String> list;
    ArrayList<String> list1;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_users, container, false);

        mySearchView = view.findViewById(R.id.searchView);
        myList = view.findViewById((R.id.myList));
        list = new ArrayList<String>();
        list1 = new ArrayList<String>();

        mAuth = FirebaseAuth.getInstance();
        collectionReference = null;

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        myList.setAdapter(adapter);


        //list.add("hello");
        //list.add("good morning");

        return view;

    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            collectionReference = FirebaseFirestore.getInstance().collection("users");
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    //Toast.makeText(getApplicationContext(),mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                    list.clear();
                    list1.clear();
                    for (int i = 0; i < data.size(); i++) {
                        if(mAuth.getCurrentUser().getEmail().compareTo(data.get(i).toObject(User.class).getEmail())!=0) {
                            list.add(data.get(i).toObject(User.class).getUserID());
                            list1.add(data.get(i).toObject(User.class).getEmail());
                            //list.add(data.get(i).toObject(User.class));
                            //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //Toast.makeText(getActivity(), myList.get(0).getEmail(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            });
        }


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
        EditText editText;
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String email= list1.get(position);
                //String email=username.getE
                Intent intent= new Intent(getContext(), search_userdata.class);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        onStart();
    }
}
