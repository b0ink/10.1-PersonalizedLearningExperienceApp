package com.example.personalizedlearningexperience;

import com.example.personalizedlearningexperience.API.AuthManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.content.Context;

public class QuizParser {
    public static ArrayList<Quiz> parseQuizzes(Context context, String json) {
        System.out.println(json);
        ArrayList<Quiz> quizzes = new ArrayList<>();
        try {
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("QuizData");
                for (int i = 0; i < jsonArray.length(); i++) {


                    JSONObject quiz = jsonArray.getJSONObject(i);
                    int id = quiz.getInt("id");
                    String topic = quiz.getString("Topic");
                    JSONArray questions = new JSONArray(quiz.getString("Questions"));

//                    JSONArray questions = quiz.getJSONArray("Questions");
                    Quiz newQuiz = new Quiz(id, topic);

                    System.out.println("New quiz " + id + topic);

                    for (int g = 0; g < questions.length(); g++) {
                        JSONObject questionObject = questions.getJSONObject(g);
                        String question = questionObject.getString("question");
                        JSONArray options = questionObject.getJSONArray("options");
                        String correctAnswer = questionObject.getString("answer");
                        ArrayList<String> quizOptions = new ArrayList<>();
                        System.out.println("New question " + correctAnswer + question);

                        for (int h = 0; h < options.length(); h++) {
                            System.out.println("New question option" + options.getString(h));

                            quizOptions.add(options.getString(h));
                        }

                        QuizQuestion newQuizQuestion = new QuizQuestion(question, quizOptions, correctAnswer);
                        newQuizQuestion.usersGuess = new AuthManager(context).getUsersGuess(newQuiz, newQuizQuestion);
                        newQuiz.AddQuestion(newQuizQuestion);
                    }


//                    String quizJSON = jsonArray.getString(2);
//                    ArrayList<QuizQuestion> questions = parseQuizQuestions(quizJSON);

//                    Quiz newQuiz = new Quiz()
                    quizzes.add(newQuiz);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quizzes;
    }
}
