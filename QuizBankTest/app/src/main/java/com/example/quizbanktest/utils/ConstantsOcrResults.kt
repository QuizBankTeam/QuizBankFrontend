package com.example.introducemyself.utils

import com.example.quizbanktest.models.OcrResultModel


object ConstantsOcrResults {
    fun getQuestions():ArrayList<OcrResultModel>{
        val list1: MutableList<String> = ArrayList()
        val list2: MutableList<String> = ArrayList()
        val list3: MutableList<String> = ArrayList()
        list1.add("Java")
        list1.add("演算法")
        list1.add("資料結構")
        list2.add("search tree")
        list2.add("graph")
        list2.add("b tree")
        list2.add("b + tree")
        list2.add("key")
        list2.add("value")
        list2.add("map")
        list3.add("第三章")
        list3.add("第四章")
        val questionList = ArrayList<OcrResultModel>()


        val que1 = OcrResultModel(
            "1",list1,
            list2,list3
        )
        questionList.add(que1)

        val que2 = OcrResultModel(
            "2",list1,
            list2,list3
        )
        questionList.add(que2)

        val que3 = OcrResultModel(
            "3",list1,
            list2,list3
        )
        questionList.add(que3)

        val que4 = OcrResultModel(
            "4",list1,
            list2,list3
        )
        questionList.add(que4)

        val que5 = OcrResultModel(
            "5",list1,
            list2,list3
        )

        return questionList
    }
}