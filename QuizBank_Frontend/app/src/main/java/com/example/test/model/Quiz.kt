package com.example.test.model

data class Quiz(val id: String?,
                var title: String?,
                val type: String?, //casual, single
                val status: String,
                val duringTime: Int?,
                val casualDuringTime: ArrayList<Int>?,
                val startDate: String?,
                val endDate: String?,
                val members: ArrayList<String>?, // user id
                var questions: ArrayList<Question>?)
