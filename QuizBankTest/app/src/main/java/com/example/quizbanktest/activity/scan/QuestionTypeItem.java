package com.example.quizbanktest.activity.scan;

public class QuestionTypeItem {
    private String mQuestionTypeName;
    private int mQuestionTypeImage;

    public QuestionTypeItem(String QuestionTypeName, int QuestionTypeImage) {
        mQuestionTypeName = QuestionTypeName;
        mQuestionTypeImage = QuestionTypeImage;
    }

    public String getQuestionTypeName() {
        return mQuestionTypeName;
    }

    public int getQuestionTypeImage() {
        return mQuestionTypeImage;
    }
}
