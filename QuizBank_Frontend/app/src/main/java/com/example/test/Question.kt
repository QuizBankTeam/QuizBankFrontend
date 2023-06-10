package com.example.test

data class Question(val id: String,
                    val title: String,
                    val number: String,//題號
                    val description: String,
                    val options: ArrayList<String>,
                    val type: String, // Filling, MultipleChoiceS, ShortAnswer, MultipleChoiceM, TrueOrFalse
                    val questionBank: String, //questionBank id
                    val answerOption: ArrayList<String>,
                    val answerDescription: String,
                    val provider: String, // user id
                    val image: Int, // base64
                    val tag: ArrayList<String>,
                    val createdDate: String)
