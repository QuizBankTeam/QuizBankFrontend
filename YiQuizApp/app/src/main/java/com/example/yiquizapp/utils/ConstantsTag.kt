package com.example.introducemyself.utils

object ConstantsTag {
    fun getList1():MutableList<String>{
        val list1: MutableList<String> = ArrayList()
        list1.add("Java")
        list1.add("C++")
        list1.add("Python")
        list1.add("Swift")
        list1.add("kotlin")
        list1.add("計算機組織")
        list1.add("線性代數")
        list1.add("離散數學")
        list1.add("作業系統")
        list1.add("演算法")
        list1.add("資料結構")
        return list1
    }
    fun getList2():MutableList<String>{
        val list2: MutableList<String> = ArrayList()
        list2.add("矩陣")
        list2.add("search tree")
        list2.add("graph")
        list2.add("b tree")
        list2.add("b + tree")
        list2.add("三角函數")
        list2.add("race condition")
        list2.add("key")
        list2.add("value")
        list2.add("map")
        list2.add("list")
        list2.add("while")
        list2.add("for")
        return list2
    }
    fun getList3():MutableList<String>{
        val list3 : MutableList<String> = ArrayList()
        list3.add("第一章")
        list3.add("第二章")
        list3.add("第三章")
        list3.add("第四章")
        list3.add("第五章")
        list3.add("第六章")
        return list3
    }
    fun getEmptyList():MutableList<String>{
        val list: MutableList<String> = ArrayList()
        list.add("目前為空喔")
        return list
    }

}