package com.example.quizbanktest.utils.group

import com.example.quizbanktest.models.GroupModel

object ConstantsGroup {
    var groupList = ArrayList<GroupModel>()
    init {
        var members  = ArrayList<String>()
        members.add("wcy")
        members.add("cy")
        members.add("wyt")
        members.add("jacky")
        members.add("youxi")
        groupList.clear()
        var group1 : GroupModel = GroupModel("1","","演算法群組", members = members, chatroom = ConstantsChatRoom.chatRoom1)
        var group2 : GroupModel = GroupModel("2","","資料結構群組", members = members,chatroom = ConstantsChatRoom.chatRoom2)
        var group3 : GroupModel = GroupModel("3","","線性代數群組", members = members,chatroom = ConstantsChatRoom.chatRoom3)
        var group4 : GroupModel = GroupModel("4","","離散數學群組", members = members,chatroom = ConstantsChatRoom.chatRoom4)
        var group5 : GroupModel = GroupModel("5","","作業系統群組", members = members,chatroom = ConstantsChatRoom.chatRoom5)
        var group6 : GroupModel = GroupModel("6","","記組群組", members = members,chatroom = ConstantsChatRoom.chatRoom6)
        groupList.add(group1)
        groupList.add(group2)
        groupList.add(group3)
        groupList.add(group4)
        groupList.add(group5)
        groupList.add(group6)

    }
}