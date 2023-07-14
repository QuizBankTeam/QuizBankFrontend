package com.example.yiquizapp.utils

import com.example.yiquizapp.models.QuestionModel

object ConstantsWrong {

    fun getQuestions():ArrayList<QuestionModel>{
        val questionList = ArrayList<QuestionModel>()
        val options: ArrayList<String> = ArrayList()
        options.add("test1")
        val answerOptions: ArrayList<String> = ArrayList()
        answerOptions.add("answer1")
        val image: ArrayList<String> = ArrayList()
        val tag: ArrayList<String> = ArrayList()
        var que1 = QuestionModel(
            "123",
            "wrong test",
            "1234567890",
            "This is a test.",
            options,
            "MultipleChoiceS",
            "single",
            "224171b8-cb82-4eac-9601-6bfd03869e5a",
            answerOptions,
            "This is test2.",
            "fc29a730-2162-4a7c-a7e7-8a664987d8a7", // user id
            "2023-6-29",
            image,   //todo
            tag
        )
        questionList.add(que1)

        val que2 = QuestionModel(
            "123",
            "wrong test2",
            "1234567890",
            "This is a test.",
            options,
            "MultipleChoiceS",
            "single",
            "224171b8-cb82-4eac-9601-6bfd03869e5a",
            answerOptions,
            "This is test2.",
            "fc29a730-2162-4a7c-a7e7-8a664987d8a7", // user id
            "2023-6-29",
            image,   //todo
            tag

        )
        questionList.add(que2)

        val que3 = QuestionModel(
            "123",
            "wrong test3",
            "1234567890",
            "This is a test.",
            options,
            "MultipleChoiceS",
            "single",
            "224171b8-cb82-4eac-9601-6bfd03869e5a",
            answerOptions,
            "This is test2.",
            "fc29a730-2162-4a7c-a7e7-8a664987d8a7", // user id
            "2023-6-29",
            image,   //todo
            tag
        )
        questionList.add(que3)

        val que4 = QuestionModel(
            "123",
            "wrong test4",
            "1234567890",
            "This is a test.",
            options,
            "MultipleChoiceS",
            "single",
            "224171b8-cb82-4eac-9601-6bfd03869e5a",
            answerOptions,
            "This is test2.",
            "fc29a730-2162-4a7c-a7e7-8a664987d8a7", // user id
            "2023-6-29",
            image,   //todo
            tag
        )
        questionList.add(que4)
        return questionList
    }
}