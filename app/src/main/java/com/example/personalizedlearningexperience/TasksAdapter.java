package com.example.personalizedlearningexperience;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    public ArrayList<Quiz> tasks;


    public TasksAdapter(Context context, ArrayList<Quiz> interests){
        this.tasks = interests;
    }


    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_task_fragment, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TasksViewHolder holder, int position) {
        Quiz quiz = tasks.get(position);
        holder.bind(quiz);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TasksViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlTaskView;

        private TextView tvQuizTitle;
        private TextView tvQuizDescription;
        private Button btnAttemptQuiz;


        public TasksViewHolder(@NonNull View itemView){
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tvTitle);
            tvQuizDescription = itemView.findViewById(R.id.tvDescription);
            btnAttemptQuiz = itemView.findViewById(R.id.btnStartQuiz);
            rlTaskView = itemView.findViewById(R.id.rlTaskView);
        }

        public void bind(Quiz quiz){
//            tvInterestTitle.setText(quiz);
            if(quiz.topic.equals("Generating your personalised quiz...")){
                tvQuizTitle.setText(quiz.topic);
                btnAttemptQuiz.setVisibility(View.GONE);
                tvQuizDescription.setText("");
            }else{
                tvQuizTitle.setText("Quiz: " + quiz.topic);
                tvQuizDescription.setText("AI generated quiz about the topic " + quiz.topic + "!");
            }



            btnAttemptQuiz.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quiz.id);
                view.getContext().startActivity(intent);
            });
        }

    }

}
