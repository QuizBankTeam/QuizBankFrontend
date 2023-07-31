package com.example.quizbanktest.models

import java.io.Serializable

data class QuestionSetModel (
    val _id: String,
    val description: String,
    val questionBank: String,
    val createdDate: String,
    val questions : ArrayList<QuestionModel>,
    val image : ArrayList<String>,
    val originateFrom : String,
    val provider:String
): Serializable