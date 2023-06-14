package com.example.quizbanktest.models

import java.io.Serializable

data class QuestionModel(
    val id: String,
    val title: String,
    val description: String,
    val options: String,
    val answerOption:String,
    val answerDescription:String,
    val provider:String,
    val image:String,
    val tag:String,
    val createDate:String

): Serializable
//  id: string,
//    title: string,
//    number: string,//題號
//    description: string,
//    options: Array<string>,
//    type: string, // Filling, MultipleChoiceS, ShortAnswer, MultipleChoiceM, TrueOrFalse
//    questionBank: string, //questionBank id
//    answerOption: Array<string>,
//    answerDescription: string,
//    provider: string, // user id
//    image: Array<string>, // base64
//    tag: Array<string>,
//    createdDate: string,