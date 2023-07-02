package com.example.test.model

data class QuizRecord(val id: String,
                      val title: String,
                      val quizId: String,
                      val date: String,
                      val type: String, //casual, single
                      val totalScore:Int,
                      val duringTime: Int?,
                      val startDate: String,
                      val endDate: String,
                      val members: ArrayList<String>?, // user id
                      val questionRecords: ArrayList<String>) // question record id)
