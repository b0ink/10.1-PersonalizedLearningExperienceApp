package com.example.personalizedlearningexperience;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalizedlearningexperience.API.AuthManager;
import com.example.personalizedlearningexperience.API.RetrofitClient;
import com.example.personalizedlearningexperience.API.models.ResponsePost;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.QuizQuestionViewHolder> {

    public ArrayList<QuizQuestion> questions;

    private Context context;

    public QuizQuestionAdapter(Context context, ArrayList<QuizQuestion> questions) {
        this.questions = questions;
        this.context = context;
    }


    @NonNull
    @Override
    public QuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_question_fragment, parent, false);
        return new QuizQuestionViewHolder(context, view);
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
        private TextView tvQuestionResult;

        private RadioGroup rgOptions;
        private RadioButton rbtnOption1;
        private RadioButton rbtnOption2;
        private RadioButton rbtnOption3;
        private RadioButton rbtnOption4;

        private Button btnSubmitAnswer;


        // AI Feedback View
        private RelativeLayout rlFeedbackView;
        private TextView tvFeedbackTitle;
        private TextView tvFeedbackText;
        private Button btnBackToQuestion;
        private GifImageView gifFeedbackSpinner;

        private boolean displayFeedback = false;

        // TODO: auto-expand the first question?
        private boolean isExpanded = false;
        private Context context;

        public QuizQuestionViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            this.context = context;
            rlQuestionView = itemView.findViewById(R.id.rlQuestionView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvExpandTooltip = itemView.findViewById(R.id.tvExpandTooltip);
            tvQuestionStatus = itemView.findViewById(R.id.tvQuestionStatus);
            tvQuestionResult = itemView.findViewById(R.id.tvQuestionResult);

            rgOptions = itemView.findViewById(R.id.rgOptions);
            rbtnOption1 = itemView.findViewById(R.id.rbtnOption1);
            rbtnOption2 = itemView.findViewById(R.id.rbtnOption2);
            rbtnOption3 = itemView.findViewById(R.id.rbtnOption3);
            rbtnOption4 = itemView.findViewById(R.id.rbtnOption4);

            btnSubmitAnswer = itemView.findViewById(R.id.btnSubmitAnswer);
            gifFeedbackSpinner = itemView.findViewById(R.id.gifFeedbackSpinner);

            rlFeedbackView = itemView.findViewById(R.id.rlFeedbackView);
            tvFeedbackTitle = itemView.findViewById(R.id.tvFeedbackTitle);
            tvFeedbackText = itemView.findViewById(R.id.tvFeedbackText);
            btnBackToQuestion = itemView.findViewById(R.id.btnBackToQuestion);

        }

        public void bind(QuizQuestion question) {
            if (question == null) return;
            String index = String.valueOf(getAdapterPosition() + 1);
            tvQuestion.setText(index + ". " + question.question);
            rbtnOption1.setText(question.options.get(0));
            rbtnOption2.setText(question.options.get(1));
            rbtnOption3.setText(question.options.get(2));
            rbtnOption4.setText(question.options.get(3));


            rlQuestionView.setOnClickListener(view -> {
                if (isExpanded) {
                    collapseRadioGroup();
                } else {
                    expandRadioGroup();
                    if (!question.usersGuess.isEmpty()) {
                        btnSubmitAnswer.setVisibility(View.VISIBLE);
                    }
                }
                isExpanded = !isExpanded;
            });

            if (!isExpanded) collapseRadioGroup();

            rgOptions.setOnCheckedChangeListener((group, checkedID) -> {
                markAnswered();
                RadioButton radioButton = itemView.findViewById(checkedID);
                question.usersGuess = radioButton.getText().toString();
                System.out.println("users guess: " + radioButton.getText().toString());
            });

            tvQuestionResult.setVisibility(View.GONE);
            btnSubmitAnswer.setVisibility(View.GONE);
            rlFeedbackView.setVisibility(View.GONE);
            rlFeedbackView.setRotationY(180f);
            btnSubmitAnswer.setOnClickListener(view -> {
                System.out.println("feedback button clicked - flipping card");

                if (!displayFeedback) {
                    System.out.println("About to display feedback");
                    if (tvFeedbackText.getText().toString().isEmpty()) {
                        gifFeedbackSpinner.setVisibility(View.VISIBLE);

                        String questionStr = question.question;
                        String delimiter = "\", \"";
                        String options = "[\"" + String.join(delimiter, question.options) + "\"]";
                        String correctAnswer = question.correctAnswer;
                        String usersGuess = question.usersGuess;

//                        System.out.println(options);
                        AuthManager auth = new AuthManager(context);
                        Call<ResponsePost> call = RetrofitClient.getInstance()
                                .getAPI().getQuizFeedback(auth.getToken(), questionStr, options, correctAnswer, usersGuess);

                        call.enqueue(new Callback<ResponsePost>() {
                            @Override
                            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                                if(!response.isSuccessful()){
                                    System.out.println("response not successful (feedback)");
                                    return;
                                }

                                gifFeedbackSpinner.setVisibility(View.GONE);
                                String message = response.body().message;
                                System.out.println("feedback receieved:" + message);

                                tvFeedbackText.setText(message);
                            }

                            @Override
                            public void onFailure(Call<ResponsePost> call, Throwable throwable) {
                                Toast.makeText(context, "An error has occurred, please try again", Toast.LENGTH_LONG).show();
                                flipCard(); // hide feedback and let user flip it back to attempt feedback retrieval again
                            }
                        });
                    } else {
                        gifFeedbackSpinner.setVisibility(View.GONE);
                    }
                }

                flipCard();


            });

            btnBackToQuestion.setOnClickListener(view -> {
                System.out.println("feedback button clicked - flipping card");
                flipCard();
            });

            if (!question.usersGuess.isEmpty()) {
                //TODO: are we 100% displaying the results page here?
//                btnSubmitAnswer.setVisibility(View.VISIBLE);

                markAnswered();
                rbtnOption1.setEnabled(false);
                rbtnOption2.setEnabled(false);
                rbtnOption3.setEnabled(false);
                rbtnOption4.setEnabled(false);
                RadioButton rbtnUsersGuess = null;
                if (rbtnOption1.getText().toString().equals(question.usersGuess)) {
                    rbtnUsersGuess = rbtnOption1;
                }
                if (rbtnOption2.getText().toString().equals(question.usersGuess)) {
                    rbtnUsersGuess = rbtnOption2;
                }
                if (rbtnOption3.getText().toString().equals(question.usersGuess)) {
                    rbtnUsersGuess = rbtnOption3;
                }
                if (rbtnOption4.getText().toString().equals(question.usersGuess)) {
                    rbtnUsersGuess = rbtnOption4;
                }
                rbtnUsersGuess.setChecked(true);
                rbtnUsersGuess.setTypeface(null, Typeface.BOLD);
                tvQuestionResult.setTypeface(null, Typeface.BOLD);
                tvQuestionResult.setVisibility(View.VISIBLE);

                if (question.usersGuess.equals(question.correctAnswer)) {
                    // answer is correct
                    rbtnUsersGuess.setTextColor(Color.parseColor("#00ff45"));
                    tvQuestionResult.setText("Correct");
                    tvQuestionResult.setTextColor(Color.parseColor("#00ff45"));
                } else {
                    rbtnUsersGuess.setTextColor(Color.parseColor("#ff0000"));
                    tvQuestionResult.setText("Incorrect");
                    tvQuestionResult.setTextColor(Color.parseColor("#FF0000"));

                    //TODO: then theres no need to find users guess?:
                    if (rbtnOption1.getText().toString().equals(question.correctAnswer)) {
                        rbtnOption1.setTextColor(Color.parseColor("#00ff45"));
                        rbtnOption1.setTypeface(null, Typeface.BOLD);
                    }
                    if (rbtnOption2.getText().toString().equals(question.correctAnswer)) {
                        rbtnOption2.setTextColor(Color.parseColor("#00ff45"));
                        rbtnOption2.setTypeface(null, Typeface.BOLD);
                    }
                    if (rbtnOption3.getText().toString().equals(question.correctAnswer)) {
                        rbtnOption3.setTextColor(Color.parseColor("#00ff45"));
                        rbtnOption3.setTypeface(null, Typeface.BOLD);
                    }
                    if (rbtnOption4.getText().toString().equals(question.correctAnswer)) {
                        rbtnOption4.setTextColor(Color.parseColor("#00ff45"));
                        rbtnOption4.setTypeface(null, Typeface.BOLD);
                    }

                }
//                rgOptions.setEnabled(false);
            } else {
                markUnanswered();
            }

//            btnSubmitAnswer.setOnClickListener(view -> {
////                Intent intent = new Intent(view.getContext(), QuizActivity.class);
////                intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quiz.id);
////                view.getContext().startActivity(intent);
//            });
        }

        private void markUnanswered() {
            tvQuestionStatus.setText("Unanswered");
            tvQuestionStatus.setTextColor(Color.parseColor("#B2D7FF"));
            tvQuestionStatus.setBackgroundResource(R.drawable.question_status_background_unanswered);
        }

        private void markAnswered() {
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


            ObjectAnimator animator = ObjectAnimator.ofFloat(rgOptions, "alpha", 0f, 1f);
            animator.setDuration(500);
            animator.start();
            tvExpandTooltip.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tvExpandTooltip.setText("Click to collapse");
        }

        private void flipCard() {
            // Define the rotation animation
            ObjectAnimator anim = ObjectAnimator.ofFloat(displayFeedback ? rlFeedbackView : rlQuestionView,
                    "rotationY", 0f, 90f);
            anim.setDuration(250); // Duration in milliseconds

            ObjectAnimator anim2 = ObjectAnimator.ofFloat(displayFeedback ? rlQuestionView : rlFeedbackView,
                    "rotationY", -90f, 0);
            anim2.setDuration(250); // Duration in milliseconds

            // At the midpoint of the animation, toggle visibility
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    displayFeedback = !displayFeedback;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    if (displayFeedback) {
                        rlQuestionView.setVisibility(View.INVISIBLE);
                        rlFeedbackView.setVisibility(View.VISIBLE);
//                        rlFeedbackView.setRotationY(0);
                    } else {
                        rlQuestionView.setVisibility(View.VISIBLE);
//                        rlQuestionView.setRotationY(0);

                        rlFeedbackView.setVisibility(View.GONE);
                    }
//                    displayFeedback = !displayFeedback;

                    anim2.start();

                }
            });

            anim.start();
        }

    }

}
