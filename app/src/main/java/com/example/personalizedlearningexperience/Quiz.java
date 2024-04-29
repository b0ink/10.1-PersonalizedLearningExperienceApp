package com.example.personalizedlearningexperience;

import java.util.ArrayList;

public class Quiz {
    public int id;
    public String topic;
    public ArrayList<QuizQuestion> questions;

    public Quiz(int id, String topic, ArrayList<QuizQuestion> questions) {
        this.id = id;
        this.topic = topic;
        this.questions = questions;
    }

}
