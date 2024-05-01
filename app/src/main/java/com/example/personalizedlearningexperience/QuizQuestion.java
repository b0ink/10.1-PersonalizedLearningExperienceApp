package com.example.personalizedlearningexperience;

import java.util.ArrayList;

public class QuizQuestion {
    public String question;
    public ArrayList<String> options;
    public String correctAnswer;

    public String usersGuess;

    public QuizQuestion(String question, ArrayList<String> options, String correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.usersGuess = "";
    }
}
