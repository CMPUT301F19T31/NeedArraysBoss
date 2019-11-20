package com.example.moodtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers){
        this.mContext = mContext;
        this.mUsers = mUsers;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
     
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        viewHolder.btn_follow.setVisibility(View.VISIBLE);
        isFollowing(user.getUserID(), viewHolder.btn_follow);

        viewHolder.userID.setText(user.getUserID());
        if (user.getUserID().equals(firebaseUser.getUid())) {
            viewHolder.btn_follow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profield", user.getUserID());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();

            }
        });
        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.btn_follow.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getEmail())
                            .child("following").child(user.getEmail()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getEmail())
                            .child("followers").child(firebaseUser.getEmail()).setValue(true);


                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getEmail())
                            .child("following").child(user.getEmail()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getEmail())
                            .child("followers").child(firebaseUser.getEmail()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userID;
        public TextView email;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            email=itemView.findViewById(R.id.email);

            //userID=itemView.findViewById(R.id.userID);
            btn_follow=itemView.findViewById(R.id.btn_follow);


        }
    }
    private void isFollowing(final String email, final Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getEmail()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(email).exists()){
                    button.setText("following");
                } else{
                    button.setText("follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
