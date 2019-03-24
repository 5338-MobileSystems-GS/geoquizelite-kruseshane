package edu.gsu.csci5338.geoquizelite;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QuestionBank {

    private Question [] qBank;

    public QuestionBank() {

    }

    public void populateBank() {
        qBank = new Question[] {
                new Question(R.string.question_1, true),
                new Question(R.string.question_2, false),
                new Question(R.string.question_3, false),
                new Question(R.string.question_4, false),
                new Question(R.string.question_5, true),
                new Question(R.string.question_6, true),
                new Question(R.string.question_7, true),
                new Question(R.string.question_8, true),
                new Question(R.string.question_9, false),
                new Question(R.string.question_10, false)
        };
    }

    public Question [] getBank() {
        return this.qBank;
    }
}
