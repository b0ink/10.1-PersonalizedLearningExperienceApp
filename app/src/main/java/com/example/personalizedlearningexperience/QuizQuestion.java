package com.example.personalizedlearningexperience;

import java.util.ArrayList;

//TODO: use localstorage/sqlite to index quizID:topic:question = user's answer for history control

public class QuizQuestion {
    public ArrayList<String> options;
    public String correctAnswer;

    public QuizQuestion(ArrayList<String> options, String correctAnswer) {
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
}
