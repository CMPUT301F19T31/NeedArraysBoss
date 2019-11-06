package com.example.moodio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class MoodListAdapter extends RecyclerView.Adapter<MoodListAdapter.MoodVH> {

    private ArrayList<Mood> moodHistory;
    private HashMap<String, Integer> moodColors;
    private HashMap<String, String> moodEmojis;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    public void setOnClickListener (OnItemClickListener listener) {
        clickListener = listener;
    }

    public static class MoodVH extends RecyclerView.ViewHolder {

        RelativeLayout rl;
        EmojiTextView feeling, reason;
        TextView socialState;

        public MoodVH(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            rl = itemView.findViewById(R.id.relativeLayout);
            feeling = itemView.findViewById(R.id.feelingVH);
            reason = itemView.findViewById(R.id.reasonVH);
            socialState = itemView.findViewById(R.id.socialStateVH);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int index = getAdapterPosition();
                        if(index != RecyclerView.NO_POSITION)
                            listener.onItemClick(index);
                    }
                }
            });
        }
    }

    public MoodListAdapter(ArrayList<Mood> moodHistory) {
        this.moodHistory = moodHistory;

        //initializes colors array
        moodColors = new HashMap<>();
        moodColors.put("happy", R.color.happy);
        moodColors.put("excited", R.color.excited);
        moodColors.put("hopeful", R.color.hopeful);
        moodColors.put("satisfied", R.color.satisfied);
        moodColors.put("sad", R.color.sad);
        moodColors.put("angry", R.color.angry);
        moodColors.put("frustrated", R.color.frustrated);
        moodColors.put("confused", R.color.confused);
        moodColors.put("annoyed", R.color.annoyed);
        moodColors.put("hopeless", R.color.hopeless);
        moodColors.put("lonely", R.color.lonely);

        //initializes emoji array
        moodEmojis = new HashMap<>();
        moodEmojis.put("happy", new String(Character.toChars(0x1F601)));
        moodEmojis.put("excited", new String(Character.toChars(0x1F606)));
        moodEmojis.put("hopeful", new String(Character.toChars(0x1F60A)));
        moodEmojis.put("satisfied", new String(Character.toChars(0x1F60C)));
        moodEmojis.put("sad", new String(Character.toChars(0x1F61E)));
        moodEmojis.put("angry", new String(Character.toChars(0x1F621)));
        moodEmojis.put("frustrated", new String(Character.toChars(0x1F623)));
        moodEmojis.put("confused", new String(Character.toChars(0x1F635)));
        moodEmojis.put("annoyed", new String(Character.toChars(0x1F620)));
        moodEmojis.put("hopeless", new String(Character.toChars(0x1F625)));
        moodEmojis.put("lonely", new String(Character.toChars(0x1F614)));

    }

    @NonNull
    @Override
    public MoodVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.moodcardview, parent, false);
        MoodVH mvh = new MoodVH(v, clickListener);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MoodVH holder, int position) {
        Mood mood = moodHistory.get(position);

        holder.feeling.setText(mood.getFeeling() + " " + moodEmojis.get(mood.getFeeling()));
        holder.reason.setText(mood.getReason());
        holder.socialState.setText(mood.getSocialState());
        holder.rl.setBackgroundResource(moodColors.get(mood.getFeeling()));
    }

    @Override
    public int getItemCount() {
        return moodHistory.size();
    }
}
