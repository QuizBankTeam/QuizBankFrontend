package com.example.quizbanktest.models

import java.io.Serializable

data class ChatRoomModel(
    val _id: String,
    val name: String,
    val messages: ArrayList<MessageModel>,
    val createdDate:String
): Serializable

