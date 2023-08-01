package com.example.quizbanktest.utils.group

import com.example.quizbanktest.models.ChatRoomModel
import com.example.quizbanktest.models.GroupModel
import com.example.quizbanktest.models.MessageModel
import java.io.Serializable

object ConstantsChatRoom {

    var roomMsgList1 : ArrayList<MessageModel> = ArrayList<MessageModel>()
    var roomMsgList2 : ArrayList<MessageModel> = ArrayList<MessageModel>()
    var roomMsgList3 : ArrayList<MessageModel> = ArrayList<MessageModel>()
    var roomMsgList4 : ArrayList<MessageModel> = ArrayList<MessageModel>()
    var roomMsgList5 : ArrayList<MessageModel> = ArrayList<MessageModel>()
    var roomMsgList6 : ArrayList<MessageModel> = ArrayList<MessageModel>()

    var chatRoom1 : ChatRoomModel = ChatRoomModel("1","演算聊天室",roomMsgList1, "2023-8-1")
    var chatRoom2 : ChatRoomModel = ChatRoomModel("2","資料結構聊天室",roomMsgList2, "2023-8-1")
    var chatRoom3 : ChatRoomModel = ChatRoomModel("3","線性代數聊天室",roomMsgList3, "2023-8-1")
    var chatRoom4 : ChatRoomModel = ChatRoomModel("4","離散數學聊天室",roomMsgList4, "2023-8-1")
    var chatRoom5 : ChatRoomModel = ChatRoomModel("5","作業系統聊天室",roomMsgList5, "2023-8-1")
    var chatRoom6 : ChatRoomModel = ChatRoomModel("6","記組聊天室",roomMsgList6, "2023-8-1")
}

