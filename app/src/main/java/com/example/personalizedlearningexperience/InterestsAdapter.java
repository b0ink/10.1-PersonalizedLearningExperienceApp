package com.example.personalizedlearningexperience;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalizedlearningexperience.API.AuthManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder> {

    public ArrayList<String> interests;
    private ArrayList<String> selectedInterests = new ArrayList<>();


    public InterestsAdapter(Context context, ArrayList<String> interests) {
        this.interests = interests;
    }


    @NonNull
    @Override
    public InterestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_item_fragment, parent, false);
        return new InterestsViewHolder(view, selectedInterests);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestsAdapter.InterestsViewHolder holder, int position) {
        String interest = interests.get(position);
        holder.bind(interest);
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    public class InterestsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInterestTitle;
        private RelativeLayout rlInterestItem;

        ArrayList<String> selectedInterests;

        public InterestsViewHolder(@NonNull View itemView, ArrayList<String> selectedInterests) {
            super(itemView);
            tvInterestTitle = itemView.findViewById(R.id.tvInterestTitle);
            rlInterestItem = itemView.findViewById(R.id.rlInterestItem);
            this.selectedInterests = selectedInterests;
        }

        @SuppressLint("ResourceAsColor")
        public void bind(String interest) {
            tvInterestTitle.setText(interest);
            rlInterestItem.setOnClickListener(view -> {
                int index = selectedInterests.indexOf(tvInterestTitle.getText().toString());
                if (index != -1) {
//                    notifyItemMoved(getAdapterPosition(),   selectedInterests.size());
                    selectedInterests.remove(index);
//                    view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    rlInterestItem.setBackgroundResource(R.drawable.gradient_card_background);
                    tvInterestTitle.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    if (selectedInterests.size() >= 10) {
                        Toast.makeText(view.getContext(), "You cannot select more than 10 topics!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedInterests.add(tvInterestTitle.getText().toString());
                    System.out.println(selectedInterests.toString());


//                notifyItemChanged(getAdapterPosition());
//                notifyItemMoved(getAdapterPosition(), 0);

                    view.setBackgroundColor(androidx.cardview.R.color.cardview_shadow_start_color);
                    tvInterestTitle.setTextColor(Color.parseColor("#FFFFFF"));
                }

//                SharedPreferences sharePref = view.getContext().getSharedPreferences("interests", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharePref.edit();
//                editor.putString("interests", selectedInterests.toString());
//                editor.apply();
                AuthManager authManager = new AuthManager(view.getContext());
                authManager.saveInterests(selectedInterests);
            });

            if (selectedInterests.contains(interest)) {
                rlInterestItem.setBackgroundColor(androidx.cardview.R.color.cardview_shadow_start_color);
                tvInterestTitle.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                rlInterestItem.setBackgroundResource(R.drawable.gradient_card_background);
//                rlInterestItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tvInterestTitle.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

    }

}
