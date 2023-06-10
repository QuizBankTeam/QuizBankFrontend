package com.example.test

data class Quiz(val id: String,
                val title: String,
                val type: String, //casual, single
                val status: String,
                val durningTime: Int,
                val startDate: String,
                val endDate: String,
                val members: ArrayList<String>, // user id
                val questions: ArrayList<Question>)
