package com.example.quizbanktest.models

import java.io.Serializable

data class QuestionBankModel(
    val _id: String,
    val title: String,
    val questionBankType: String,
    val createdDate: String,
    val members : ArrayList<String>,
    val originateFrom : String,
    val creator : String
): Serializable

