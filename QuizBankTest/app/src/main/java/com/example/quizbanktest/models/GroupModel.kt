package com.example.quizbanktest.models

import java.io.Serializable

data class GroupModel(
    val _id: String,
    val avator: String,
    val name: String,
    val chatroom: ChatRoomModel,
    val creator : String,
    val members : ArrayList<String>,
    val createdDate:String
): Serializable
