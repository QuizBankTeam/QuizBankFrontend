package com.example.quizbanktest.models

import java.io.Serializable

data class MessageModel(
    val _id: String,
    val content: String,
    val sender: String,
    val seenBy: String,
    val createdDate:String
): Serializable

