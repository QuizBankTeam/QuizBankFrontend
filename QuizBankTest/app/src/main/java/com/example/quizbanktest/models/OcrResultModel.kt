package com.example.quizbanktest.models

import java.io.Serializable

data class OcrResultModel(
    val id: String,
    val bankList : MutableList<String>,
    val questionList: MutableList<String>,
    val rangList: MutableList<String>
): Serializable
