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

    public Quiz(int id, String topic) {
        this.id = id;
        this.topic = topic;
        questions = new ArrayList<>();
    }

    public void AddQuestion(QuizQuestion question){
        questions.add(question);
    }

    public String getFormattedTopic(){
        return this.topic.substring(0, 1).toUpperCase() + this.topic.substring(1);
    }
}
