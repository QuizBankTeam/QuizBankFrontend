package com.example.quizbanktest.utils

import com.example.quizbanktest.models.QuestionBankModel

object ConstantsQuestionBank {

    fun getQuestions():ArrayList<QuestionBankModel>{
        val questionList = ArrayList<QuestionBankModel>()

        val que1 = QuestionBankModel(
            "1","作業系統",
            "資工","2022/05/17","wcy"

        )
        questionList.add(que1)

        val que2 = QuestionBankModel(
            "2","Android",
            "資工","2022/05/17","wcy"
        )
        questionList.add(que2)

        val que3 = QuestionBankModel(
            "1","IOS",
            "資工","2022/05/17","wcy"
        )
        questionList.add(que3)

        val que4 = QuestionBankModel(
            "1","電腦網路",
            "資工","2022/05/17","wcy"
        )
        questionList.add(que4)

        val que5 = QuestionBankModel(
            "1","機器視覺",
            "資工","2022/05/17","wcy"
        )

        return questionList
    }
}