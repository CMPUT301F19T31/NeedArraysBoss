package com.example.moodtracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a custom recycler view adapter
 */
public class MoodListAdapter extends RecyclerView.Adapter<MoodListAdapter.MoodVH> {

    private ArrayList<Mood> moodHistory;
    private HashMap<String, Integer> moodColors;
    private HashMap<String, String> moodEmojis;
    private OnItemClickListener clickListener;

    /**
     * Creates the interface for the OnItemClickListener
     */
    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    /**
     * This sets the Listener
     * @param listener
     * The listener that will be set to handle item clicks
     */
    public void setOnClickListener (OnItemClickListener listener) {
        clickListener = listener;
    }


    /**
     * This is the class for the view holder for the recycler view
     */
    public static class MoodVH extends RecyclerView.ViewHolder {

        RelativeLayout rl;
        EmojiTextView feeling, reason;
        TextView socialState;
        TextView username, time;
        ImageView image;

        /**
         * This is the constructor for the view holder class
         * @param itemView
         * The view within the recycler view that was clicked
         * @param listener
         * The listener that will be set to handle item clicks
         */
        public MoodVH(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            rl = itemView.findViewById(R.id.relativeLayout);
            feeling = itemView.findViewById(R.id.feelingVH);
            reason = itemView.findViewById(R.id.reasonVH);
            socialState = itemView.findViewById(R.id.socialStateVH);
            username = itemView.findViewById(R.id.currentUserTV);
            time = itemView.findViewById(R.id.timeTV);
            image = itemView.findViewById(R.id.displayMoodImage);

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

    /**
     * This is the constructor for the mood list adapter
     * @param moodHistory
     * The array list of mood events
     */
    public MoodListAdapter(ArrayList<Mood> moodHistory) {
        this.moodHistory = moodHistory;
        initArrays();
    }

    /**
     * Initializes the arrays for feelings and social states
     */
    public void initArrays() {
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


    public ArrayList<Mood> getList () {
        return moodHistory;
    }
    public void setList(ArrayList<Mood> moodHistory) { this.moodHistory = moodHistory; }

    /**
     * Used to initialize the view holder
     * @param  parent
     * The view group that initializes the view holder
     * @param viewType
     * The savedInstanceState is a reference to a Bundle object that is passed into the onCreate method of every Android Activity.
     */
    @NonNull
    @Override
    public MoodVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mood_card_view, parent, false);
        MoodVH mvh = new MoodVH(v, clickListener);
        return mvh;
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param  holder
     * The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position
     * The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MoodVH holder, int position) {
        Mood mood = moodHistory.get(position);

        holder.feeling.setText(mood.getFeeling() + " " + moodEmojis.get(mood.getFeeling()));
        holder.reason.setText(mood.getReason());
        holder.socialState.setText(mood.getSocialState());
        if(mood.getFriend() != null)
            holder.username.setText(mood.getFriend());
        holder.time.setText("Posted " + mood.getTimeAgo());
        if(mood.getImg() == null) {
            decodeImage(mood.getImg(), holder.image);
        }

        holder.rl.setBackgroundResource(moodColors.get(mood.getFeeling()));
    }

    /**
     * Counts the number of mood events
     * @return
     * Returns the size of the adapter
     */
    @Override
    public int getItemCount() {
        return moodHistory.size();
    }
}
