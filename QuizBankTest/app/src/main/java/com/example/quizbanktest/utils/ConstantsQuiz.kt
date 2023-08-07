package com.example.quizbanktest.utils

import android.content.Context
import android.util.Log
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.network.quizService
import com.google.gson.Gson
import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit


object ConstantsQuiz {
    private val onFailureStr = "network not available"
    private var quizList = ArrayList<Quiz>()

    fun getAllQuizsWithBatch(context: Context, quizType: String, batch: Int, onSuccess: (ArrayList<Quiz>) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizService::class.java)
            val call = api.getAllQuizsWithBatch(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, quizType, batch)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val allQuizResponse = gson.fromJson(
                            response.body().charStream(),
                            quizService.AllQuizsResponse::class.java
                        )
                        quizList = allQuizResponse.quizList
                        onSuccess(allQuizResponse.quizList)
                    }
                    else{
                        onFailure("Request failed with status code ${response.code()}")
                    }
                }

                override fun onFailure(t: Throwable?) {
                    onFailure("Request failed with message ${t?.message.toString()}")
                }

            })
        }else{
            onFailure(onFailureStr)
        }
    }
    fun putQuiz(context: Context, quiz: Quiz, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val putQuestionList = ArrayList<quizService.QuestionInPutQuiz>()
            if(quiz.startDateTime.isNullOrEmpty())
                quiz.startDateTime = "none"
            if(quiz.endDateTime.isNullOrEmpty())
                quiz.endDateTime = "none"
            if(quiz.title.isNullOrEmpty())
                quiz.title = "none"
            if(quiz.casualDuringTime==null)
                quiz.casualDuringTime = ArrayList()
            if(quiz.members==null) {
                quiz.members = ArrayList()
            }
//            quiz.members!!.add(Constants.userId)

            if(quiz.questions==null) {
                quiz.questions = ArrayList()
            }else{
                for(question in quiz.questions!!){
                    if(question.title.isNullOrEmpty())
                        question.title = "Untitled"
                    if(question.description.isNullOrEmpty())
                        question.description = "none"
                    if(question.options==null)
                        question.options = ArrayList<String>()
                    if(question.answerOptions==null)
                        question.answerOptions = ArrayList<String>()
                    if(question.bankType.isNullOrEmpty())
                        question.bankType="none"
                    if(question.questionBank.isNullOrEmpty())
                        question.questionBank="none"
                    if(question.answerDescription.isNullOrEmpty())
                        question.answerDescription="none"
                    if(question.provider.isNullOrEmpty())
                        question.provider="none"
                    if(question.questionImage==null)
                        question.questionImage = ArrayList<String>()
                    if(question.answerImage==null)
                        question.answerImage = ArrayList<String>()
                    if(question.tag==null)
                        question.tag = ArrayList<String>()
                    val putQuestion = quizService.QuestionInPutQuiz(question._id!!, question.title!!, question.number!!, question.description!!, question.options!!, question.questionType!!,question.bankType!!, question.questionBank!!, question.answerOptions!!, question.answerDescription!!, question.provider!!, question.originateFrom!!, question.createdDate!!, question.answerImage!!, question.questionImage!!, question.tag!!)
                    putQuestionList.add(putQuestion)
                }
            }

            val putQuiz = quizService.PutQuiz(quiz._id!!, quiz.title!!, quiz.status!!, quiz.duringTime!!, quiz.casualDuringTime!!, quiz.startDateTime!!, quiz.endDateTime!!, quiz.members!!, putQuestionList)
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizService::class.java)
            val call = api.putQuiz(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, putQuiz)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val putQuizResponse = gson.fromJson(
                            response.body().charStream(),
                            quizService.PutQuizResponse::class.java
                        )
                        onSuccess(putQuizResponse.message)
                    }
                    else{
                        onFailure("Request failed with status code ${response.code()}")
                    }
                }
                override fun onFailure(t: Throwable?) {
                    onFailure("Request failed with message ${t?.message.toString()}")
                }

            })
        }else{
            onFailure(onFailureStr)
        }
    }
    fun deleteQuiz(context: Context, quizId: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizService::class.java)
            val call = api.deleteQuiz(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, quizId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val deleteQuizResponse = gson.fromJson(
                            response.body().charStream(),
                            quizService.DeleteQuizResponse::class.java
                        )
                        onSuccess(deleteQuizResponse.message)
                    }else{
                        onFailure("Request failed with status code ${response.code()}")
                    }
                }
                override fun onFailure(t: Throwable?) {
                    onFailure("Request failed with message ${t?.message.toString()}")
                }
            })
        }
    }
}