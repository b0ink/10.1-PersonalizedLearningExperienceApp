package com.example.personalizedlearningexperience;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.QuizQuestionViewHolder> {

    public ArrayList<QuizQuestion> questions;


    public QuizQuestionAdapter(Context context, ArrayList<QuizQuestion> questions){
        this.questions = questions;
    }


    @NonNull
    @Override
    public QuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_question_fragment, parent, false);
        return new QuizQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizQuestionAdapter.QuizQuestionViewHolder holder, int position) {
        QuizQuestion question = questions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class QuizQuestionViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlQuestionView;

        private TextView tvQuestion;
        private TextView tvExpandTooltip;

        private TextView tvQuestionStatus;

        private RadioGroup rgOptions;
        private RadioButton rbtnOption1;
        private RadioButton rbtnOption2;
        private RadioButton rbtnOption3;
        private RadioButton rbtnOption4;

        private Button btnSubmitAnswer;

        // TODO: auto-expand the first question?
        private boolean isExpanded = false;


        public QuizQuestionViewHolder(@NonNull View itemView){
            super(itemView);
            rlQuestionView = itemView.findViewById(R.id.rlQuestionView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvExpandTooltip = itemView.findViewById(R.id.tvExpandTooltip);
            tvQuestionStatus = itemView.findViewById(R.id.tvQuestionStatus);

            rgOptions = itemView.findViewById(R.id.rgOptions);
            rbtnOption1 = itemView.findViewById(R.id.rbtnOption1);
            rbtnOption2 = itemView.findViewById(R.id.rbtnOption2);
            rbtnOption3 = itemView.findViewById(R.id.rbtnOption3);
            rbtnOption4 = itemView.findViewById(R.id.rbtnOption4);

            btnSubmitAnswer = itemView.findViewById(R.id.btnSubmitAnswer);

        }

        public void bind(QuizQuestion question){
            if(question == null) return;
            String index = String.valueOf(getAdapterPosition()+1);
            tvQuestion.setText(index + ". " + question.question);
            rbtnOption1.setText(question.options.get(0));
            rbtnOption2.setText(question.options.get(1));
            rbtnOption3.setText(question.options.get(2));
            rbtnOption4.setText(question.options.get(3));

            rlQuestionView.setOnClickListener(view ->{
                if(isExpanded){
                    collapseRadioGroup();
                }else{
                    expandRadioGroup();
                }
                isExpanded = !isExpanded;
            });

            if(!isExpanded) collapseRadioGroup();

            rgOptions.setOnCheckedChangeListener((group, checkedID) -> {
                markAnswered();
            });

            markUnanswered();

            btnSubmitAnswer.setOnClickListener(view -> {
//                Intent intent = new Intent(view.getContext(), QuizActivity.class);
//                intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quiz.id);
//                view.getContext().startActivity(intent);
            });
        }

        private void markUnanswered(){
            tvQuestionStatus.setText("Unanswered");
            tvQuestionStatus.setTextColor(Color.parseColor("#B2D7FF"));
            tvQuestionStatus.setBackgroundResource(R.drawable.question_status_background_unanswered);
        }

        private void markAnswered(){
            tvQuestionStatus.setVisibility(View.GONE);
            tvQuestionStatus.setText("Answered");
            tvQuestionStatus.setTextColor(Color.parseColor("#000000"));
            tvQuestionStatus.setBackgroundResource(R.drawable.question_status_background_answered);
        }

        private void collapseRadioGroup() {
            ObjectAnimator animator = ObjectAnimator.ofFloat(rgOptions, "alpha", 1f, 0f);
            animator.setDuration(500);
            animator.start();
            rgOptions.setVisibility(View.GONE);
            btnSubmitAnswer.setVisibility(View.GONE);
            tvExpandTooltip.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            tvExpandTooltip.setText("Click to expand");
        }

        private void expandRadioGroup() {
            rgOptions.setVisibility(View.VISIBLE);
//            btnSubmitAnswer.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(rgOptions, "alpha", 0f, 1f);
            animator.setDuration(500);
            animator.start();
            tvExpandTooltip.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvExpandTooltip.setText("Click to collapse");
        }

    }

}
