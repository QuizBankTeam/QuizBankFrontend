package com.example.quizbanktest.utils

import com.example.quizbanktest.models.GroupModel
import com.example.quizbanktest.models.QuestionModel

object ConstantsGroup {
    var groupList = ArrayList<GroupModel>()
    init {
        groupList.clear()
        var group1 : GroupModel = GroupModel("1","","group1")
        var group2 : GroupModel = GroupModel("2","","group2")
        var group3 : GroupModel = GroupModel("3","","group3")
        groupList.add(group1)
        groupList.add(group2)
        groupList.add(group3)
    }
}