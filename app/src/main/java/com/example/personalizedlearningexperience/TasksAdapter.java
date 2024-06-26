package com.example.personalizedlearningexperience;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    public ArrayList<Quiz> tasks;

    private Context context;

    public TasksAdapter(Context context, ArrayList<Quiz> interests) {
        this.tasks = interests;
        this.context = context;
    }


    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_task_fragment, parent, false);
        return new TasksViewHolder(context, view);
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
        private TextView tvCompletedStatus;

        private Button btnAttemptQuiz;

        private LinearLayout layoutSpinner;
        private TextView tvSpinnerText;
        private ImageView gifSpinner;

        private Context context;

        public TasksViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tvTitle);
            tvQuizDescription = itemView.findViewById(R.id.tvDescription);
            btnAttemptQuiz = itemView.findViewById(R.id.btnStartQuiz);
            rlTaskView = itemView.findViewById(R.id.rlTaskView);

            layoutSpinner = itemView.findViewById(R.id.layoutSpinner);
            gifSpinner = itemView.findViewById(R.id.gifSpinner);
            tvSpinnerText = itemView.findViewById(R.id.tvSpinnerText);
            tvCompletedStatus = itemView.findViewById(R.id.tvCompletedStatus);
            this.context = context;
        }

        public void bind(Quiz quiz) {
//            tvInterestTitle.setText(quiz);
            ShimmerFrameLayout shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view_container);
            tvCompletedStatus.setVisibility(View.GONE);
            if (quiz.topic.equals("GENERATING QUIZ...")) {
                tvQuizTitle.setText("");
                btnAttemptQuiz.setVisibility(View.GONE);
                gifSpinner.setVisibility(View.VISIBLE);
                layoutSpinner.setVisibility(View.VISIBLE);
                tvSpinnerText.setVisibility(View.VISIBLE);

                tvQuizDescription.setText("");
                //TODO:c shimmer no work
//                shimmerFrameLayout.startShimmer(); // To start shimmer effect

            } else {
//                shimmerFrameLayout.stopShimmer();
                String quizTopic = quiz.getFormattedTopic();
                tvQuizTitle.setText(quizTopic);
                gifSpinner.setVisibility(View.GONE);
                layoutSpinner.setVisibility(View.GONE);
                tvSpinnerText.setVisibility(View.GONE);
                tvQuizDescription.setText("AI generated quiz about " + quizTopic + "!");

                if (quiz.userHasAttempted()) {
//                    btnAttemptQuiz.setVisibility(View.GONE);
                    btnAttemptQuiz.setText("View Results");
                    btnAttemptQuiz.setBackgroundColor(Color.parseColor("#FFFF02C8"));
                    btnAttemptQuiz.setTextColor(Color.parseColor("#FFFFFF"));
                    tvCompletedStatus.setVisibility(View.VISIBLE);

                }

//                ObjectAnimator animator = ObjectAnimator.ofFloat(rlTaskView, "alpha", 0f, 1f);
//                animator.setDuration(1000);
//                animator.start();


//                int greenColor = getResources().getColor(R.color.green); // Change to your green color resource
//                int whiteColor = getResources().getColor(android.R.color.white); // Default white color
//

                // "flash" the card on load
                //TODO: only apply effect to newly generated tasks

                //TODO: instead of flashing the backgorund color, highlight it with a "NEW" badge or something
                // -> color animation loses the background gradient resource
//                if(!quiz.loaded){
//                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#00ffbb"), Color.parseColor("#015fef"));
//                    colorAnimation.setDuration(1000); // Duration of the animation in milliseconds
//
//                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animator) {
//                            rlTaskView.setBackgroundColor((int) animator.getAnimatedValue());
//                        }
//                    });
//
//                    colorAnimation.start();
//                    quiz.loaded = true;
//                }

            }


            btnAttemptQuiz.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quiz.id);
                if (quiz.userHasAttempted()) {
                    intent.putExtra(QuizActivity.EXTRA_QUIZ_LOAD_RESULTS, true);
                }
                view.getContext().startActivity(intent);
                try {
                    ((Activity) context).finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

    }

}
