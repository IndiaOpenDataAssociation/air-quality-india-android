package com.idn0phl3108ed43d22s30.pojo;

/**
 * Created by Rutul on 17-06-2016.
 */
public class Community {
    private String Question, Answer;

    public Community() {

    }

    public Community(String Question, String Answer) {
        this.Question = Question;
        this.Answer = Answer;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }
}
