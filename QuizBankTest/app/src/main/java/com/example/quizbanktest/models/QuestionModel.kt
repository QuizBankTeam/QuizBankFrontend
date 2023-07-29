package com.example.quizbanktest.models

import java.io.Serializable

data class QuestionModel(
    val _id:String?=null,
    var title: String?=null, //如果使用者沒給則用ocr出來
    var number: String?=null,//題號
    var description: String,
    var options: ArrayList<String>?=null,
    var questionType: String?=null, // Filling, MultipleChoiceS, ShortAnswer, MultipleChoiceM, TrueOrFalse
    var bankType : String?=null,
    var questionBank: String?=null, //questionBank id
    var answerOptions: ArrayList<String>  ? = null, //dialog create
    var answerDescription: String  ? = null, //dialog create
    var orginateFrom: String, // user id
    var createdDate: String,
    var image: ArrayList<String> ? = null, // base64 // dialog
    var tag: ArrayList<String> ? = null

): Serializable
