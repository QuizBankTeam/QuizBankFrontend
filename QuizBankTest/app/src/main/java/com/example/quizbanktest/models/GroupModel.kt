package com.example.quizbanktest.models

import java.io.Serializable

data class GroupModel(
    val _id: String?=null,
    val avator: String?=null,
    val name: String?=null,
    val chatroom: ChatRoomModel?=null,
    val creator : String?=null,
    val members : ArrayList<String>?=null,
    val createdDate:String?=null
): Serializable
