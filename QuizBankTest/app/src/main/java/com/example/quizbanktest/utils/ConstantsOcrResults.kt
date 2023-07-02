package com.example.introducemyself.utils

import com.example.quizbanktest.models.OcrResultModel


object ConstantsOcrResults {
    val list1: MutableList<String> = ArrayList()
    val list2: MutableList<String> = ArrayList()
    val list3: MutableList<String> = ArrayList()
    val optionList : ArrayList<String> = ArrayList()

    val questionList = ArrayList<OcrResultModel>()
    val answerOptionsList : ArrayList<String> = ArrayList()


    var que1 = OcrResultModel(

        "1",list1,list2,list3, "test",
        "1234567890",
        "This is a test.",
        optionList,
        "MultipleChoiceS",
        "single",
        "224171b8-cb82-4eac-9601-6bfd03869e5a",
        answerOptionsList,
        "This is test2.",
        "fc29a730-2162-4a7c-a7e7-8a664987d8a7", // user id
        "2023-7-01",
        null,
        null
    )

    fun getQuestions():ArrayList<OcrResultModel>{
        if(optionList.size == 0){
            optionList.add("test1")
            optionList.add("test2")
            optionList.add("test3")
        }
        if(list1.size == 0){
            list1.add("Java")
            list1.add("演算法")
            list1.add("資料結構")
        }
        if(list2.size == 0){
            list2.add("search tree")
            list2.add("graph")
            list2.add("b tree")
            list2.add("b + tree")
            list2.add("key")
            list2.add("value")
            list2.add("map")
        }
        if(list3.size==0){
            list3.add("第三章")
            list3.add("第四章")
        }
        if(answerOptionsList.size == 0){
            answerOptionsList.add("test2")
        }
        if(questionList.size == 0){
            questionList.add(que1)
        }
        return questionList
    }
}