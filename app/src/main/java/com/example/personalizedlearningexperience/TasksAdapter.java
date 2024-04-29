package com.example.personalizedlearningexperience;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

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
        private ImageView gifSpinner;


        public TasksViewHolder(@NonNull View itemView){
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tvTitle);
            tvQuizDescription = itemView.findViewById(R.id.tvDescription);
            btnAttemptQuiz = itemView.findViewById(R.id.btnStartQuiz);
            rlTaskView = itemView.findViewById(R.id.rlTaskView);
            gifSpinner = itemView.findViewById(R.id.gifSpinner);
        }

        public void bind(Quiz quiz){
//            tvInterestTitle.setText(quiz);
            ShimmerFrameLayout shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view_container);

            if(quiz.topic.equals("GENERATING QUIZ...")){
                tvQuizTitle.setText(quiz.topic);
                btnAttemptQuiz.setVisibility(View.GONE);
                gifSpinner.setVisibility(View.VISIBLE);
                tvQuizDescription.setText("");
                shimmerFrameLayout.startShimmer(); // To start shimmer effect

            }else{
                shimmerFrameLayout.stopShimmer();
                String quizTopic = quiz.getFormattedTopic();
                tvQuizTitle.setText(quizTopic);
                gifSpinner.setVisibility(View.GONE);
                tvQuizDescription.setText("AI generated quiz about " + quizTopic + "!");

//                ObjectAnimator animator = ObjectAnimator.ofFloat(rlTaskView, "alpha", 0f, 1f);
//                animator.setDuration(1000);
//                animator.start();


//                int greenColor = getResources().getColor(R.color.green); // Change to your green color resource
//                int whiteColor = getResources().getColor(android.R.color.white); // Default white color
//

                // "flash" the card on load
                //TODO: only apply effect to newly generated tasks

                if(!quiz.loaded){
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#89e89f"), Color.parseColor("#FFFFFF"));
                    colorAnimation.setDuration(1000); // Duration of the animation in milliseconds

                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            rlTaskView.setBackgroundColor((int) animator.getAnimatedValue());
                        }
                    });

                    colorAnimation.start();
                    quiz.loaded = true;
                }

            }



            btnAttemptQuiz.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quiz.id);
                view.getContext().startActivity(intent);
            });
        }

    }

}
