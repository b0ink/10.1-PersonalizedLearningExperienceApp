package com.example.personalizedlearningexperience;

import java.util.ArrayList;

public class Quiz {
    public int id;
    public String topic;
    public ArrayList<QuizQuestion> questions;

    public Boolean loaded = false;

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

    public Boolean userHasAttempted() {
        for (QuizQuestion q : this.questions) {
            if (!q.usersGuess.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public int getCorrectAnswers() {
        int correct = 0;
        for (QuizQuestion q : this.questions) {
//            System.out.println(q.usersGuess + " --- " + q.correctAnswer);
            if (q.usersGuess.equals(q.correctAnswer)) {
                correct++;
            }
        }
        return correct;
    }

    public int getTotalQuestions() {
        return this.questions.size();
    }

    public int getWrongAnswers() {
        return getTotalQuestions() - getCorrectAnswers();
    }


    public void AddQuestion(QuizQuestion question) {
        questions.add(question);
    }

    public String getFormattedTopic() {
        return (this.topic.substring(0, 1).toUpperCase() + this.topic.substring(1)).trim();
    }
}
