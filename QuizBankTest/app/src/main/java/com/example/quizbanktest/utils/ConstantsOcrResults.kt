package com.example.introducemyself.utils

import com.example.quizbanktest.activity.scan.QuestionTypeItem
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.models.ScanQuestionModel
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


object ConstantsOcrResults {
    var questionList = ArrayList<ScanQuestionModel>()
    var questionTypeList = ArrayList<String>()
    var questionTypeSpinner = ArrayList<QuestionTypeItem>()
    var rescanPosition = 0


    fun setOcrResult(description : String){

        if(questionTypeList.size == 0){
            questionTypeList.add("Filling")
            questionTypeList.add("MultipleChoiceS")
            questionTypeList.add("ShortAnswer")
            questionTypeList.add("MultipleChoiceM")
            questionTypeList.add("TrueOrFalse")
        }

        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        val formattedDate = formatter.format(currentDate)
        val options : ArrayList<String> = ArrayList()
        val answerOptions : ArrayList<String> = ArrayList()
        answerOptions.add("empty")
        val images : ArrayList<String> = ArrayList()
        val answerImages : ArrayList<String> = ArrayList()
        val tag : ArrayList<String> = ArrayList()

        val ocrResult = ScanQuestionModel(bankType = "single",options=options, answerOptions = answerOptions, image = images,answerImages = answerImages, tag = tag,description = description, createdDate = formattedDate.toString(), originateFrom = ConstantsAccountServiceFunction.userAccount!!._id)
        questionList.add(ocrResult)
    }
    fun addEmptyScanResult(){

        if(questionTypeList.size == 0){
            questionTypeList.add("Filling")
            questionTypeList.add("MultipleChoiceS")
            questionTypeList.add("ShortAnswer")
            questionTypeList.add("MultipleChoiceM")
            questionTypeList.add("TrueOrFalse")
        }

        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        val formattedDate = formatter.format(currentDate)
        val options : ArrayList<String> = ArrayList()
        val answerOptions : ArrayList<String> = ArrayList()
        val images : ArrayList<String> = ArrayList()
        val answerImages : ArrayList<String> = ArrayList()
        val tag : ArrayList<String> = ArrayList()

        val ocrResult = ScanQuestionModel(bankType = "single",options=options, answerOptions = answerOptions, image = images,answerImages = answerImages, tag = tag,description = "請新增題目描述", createdDate = formattedDate.toString(), originateFrom = ConstantsAccountServiceFunction.userAccount!!._id)
        questionList.add(ocrResult)
    }
    fun getOcrResult():ArrayList<ScanQuestionModel>{
        return  questionList
    }

}