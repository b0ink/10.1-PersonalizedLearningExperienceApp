package com.example.personalizedlearningexperience;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder> {

    public ArrayList<String> interests;


    public InterestsAdapter(Context context, ArrayList<String> interests){
        this.interests = interests;
    }


    @NonNull
    @Override
    public InterestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_item_fragment, parent, false);
        return new InterestsViewHolder(view);
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

        public InterestsViewHolder(@NonNull View itemView){
            super(itemView);
            tvInterestTitle = itemView.findViewById(R.id.tvInterestTitle);
        }

        public void bind(String interest){
            tvInterestTitle.setText(interest);
        }

    }

}
