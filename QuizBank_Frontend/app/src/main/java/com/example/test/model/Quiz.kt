package com.example.test.model

data class Quiz(val id: String?,
                var title: String?,
                var type: String?, //casual, single
                var status: String,
                var duringTime: Int?,
                var casualDuringTime: ArrayList<Int>?,
                var startDate: String?,
                var endDate: String?,
                var members: ArrayList<String>?, // user id
                var questions: ArrayList<Question>?)
