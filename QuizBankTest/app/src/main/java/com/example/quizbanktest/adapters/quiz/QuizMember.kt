package com.example.quizbanktest.adapters.quiz
import java.io.Serializable
data class QuizMember(val userName: String, var userScore: Int,
                      val userID: String, var correctAnswerNum: Int,
                      val records: ArrayList< ArrayList<String>>): Serializable
