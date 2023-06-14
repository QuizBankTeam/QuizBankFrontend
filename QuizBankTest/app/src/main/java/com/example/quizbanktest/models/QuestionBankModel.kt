package com.example.quizbanktest.models

import java.io.Serializable

data class QuestionBankModel(
    val id: String,
    val title: String,
    val type: String,
    val date: String,
    val creator:String
): Serializable
