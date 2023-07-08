package com.example.introducemyself.utils

import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


object ConstantsOcrResults {
    var questionList = ArrayList<QuestionModel>()
    var questionTypeList = ArrayList<String>()

    fun setOcrResult(description : String){
        if(questionTypeList.size == 0){
            questionTypeList.add("Filling")
            questionTypeList.add("MultipleChoiceS")
            questionTypeList.add("ShortAnswer")
            questionTypeList.add("TrueOrFalse")
        }

        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        val formattedDate = formatter.format(currentDate)
        val options : ArrayList<String> = ArrayList()
        val answerOptions : ArrayList<String> = ArrayList()
        val images : ArrayList<String> = ArrayList()
        val tag : ArrayList<String> = ArrayList()
        tag.add("123")
        tag.add("cs design")
        val ocrResult = QuestionModel(bankType = "single",options=options, answerOptions = answerOptions, image = images, tag = tag,description = description, createdDate = formattedDate.toString(), orginateFrom = ConstantsAccountServiceFunction.userAccount!!._id)
        questionList.add(ocrResult)
    }
    fun getOcrResult():ArrayList<QuestionModel>{
        return  questionList
    }

}