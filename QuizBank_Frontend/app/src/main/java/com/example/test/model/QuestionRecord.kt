package com.example.test.model

data class QuestionRecord(val id: String,
                          val user: String,
                          val userAnswer: String,
                          val correct: Boolean?, //correct or incorrect
                          val date: String,
                          val question: String, // question id
                          val quizRecord: String) // quizRecord id)
