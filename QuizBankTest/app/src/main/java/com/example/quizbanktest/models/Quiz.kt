package com.example.quizbanktest.models

data class Quiz(val _id: String?,
                var title: String?,
                var type: String, //casual, single
                var status: String?,
                var duringTime: Int?,
                var casualDuringTime: ArrayList<Int>?,
                var startDateTime: String?,
                var endDateTime: String?,
                var members: ArrayList<String>?, // user id
                var questions: ArrayList<Question>?)
