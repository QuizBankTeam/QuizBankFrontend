package com.example.quizbanktest.utils

import com.example.quizbanktest.models.QuestionModel

object ConstantsRecommend {

    fun getQuestions():ArrayList<QuestionModel>{
        val questionList = ArrayList<QuestionModel>()

        val que1 = QuestionModel(
            "1","In a hypertonic solution, a bacterial cell will typically?",
            "none","(A) lyse","(B) burst","(C) stay the same","(D) plasmolyze","none","college","2023/5/17"

        )
        questionList.add(que1)

        val que2 = QuestionModel(
            "1","In a hypertonic solution, a bacterial cell will typically?",
            "none","(A) lyse","(B) burst","(C) stay the same","(D) plasmolyze","none","college","2023/5/17"

        )
        questionList.add(que2)

        val que3 = QuestionModel(
            "1","In a hypertonic solution, a bacterial cell will typically?",
            "none","(A) lyse","(B) burst","(C) stay the same","(D) plasmolyze","none","college","2023/5/17"

        )
        questionList.add(que3)

        val que4 = QuestionModel(
            "1","In a hypertonic solution, a bacterial cell will typically?",
            "none","(A) lyse","(B) burst","(C) stay the same","(D) plasmolyze","none","college","2023/5/17"

        )
        questionList.add(que4)


        return questionList
    }
}