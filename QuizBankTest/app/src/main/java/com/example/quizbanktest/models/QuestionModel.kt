package com.example.quizbanktest.models

import java.io.Serializable

data class QuestionModel(
    val _id:String,
    var title: String, //如果使用者沒給則用ocr出來
    var number: String,//題號
    var description: String,
    var options: ArrayList<String>,
    var questionType: String, // Filling, MultipleChoiceS, ShortAnswer, MultipleChoiceM, TrueOrFalse
    var bankType : String,
    var questionBank: String, //questionBank id
    var answerOptions: ArrayList<String>, //dialog create
    var answerDescription: String, //dialog create
    var originateFrom: String, // user id
    var createdDate: String,
    var questionImage: ArrayList<String>, // base64 // dialog
    var answerImage: ArrayList<String>? = null, // base64 // dialog
    var tag: ArrayList<String>

): Serializable
