package com.example.quizbanktest.utils

import android.content.Context
import android.util.Log
import com.example.quizbanktest.R
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.network.quizService
import com.google.gson.Gson
import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


object ConstantsQuiz {
    private val onFailureStr = "network not available"
    private var quizList = ArrayList<Quiz>()

    fun getAllQuizsWithBatch(context: Context, quizType: String, batch: Int, onSuccess: (ArrayList<Quiz>?) -> Unit, onFailure: (String) -> Unit) {
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

    fun getSingleQuiz(context: Context, quizId: String, onSuccess: (Quiz) -> Unit, onFailure: (String) -> Unit){
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizService::class.java)
            val call = api.getSingleQuiz(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, quizId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val quizResponse = gson.fromJson(
                            response.body().charStream(),
                            quizService.GetQuizResponse::class.java
                        )
                        onSuccess(quizResponse.quiz)
                    } else {
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
    fun postQuiz(context: Context, quiz: quizService.PostQuiz ,onSuccess: (Quiz) -> Unit, onFailure: (String) -> Unit){
        if (Constants.isNetworkAvailable(context)) {
            if(quiz.startDateTime.isNullOrEmpty())
                quiz.startDateTime = LocalDateTime.now().format(Constants.dateTimeFormat)
            if(quiz.endDateTime.isNullOrEmpty())
                quiz.endDateTime = LocalDateTime.now().format(Constants.dateTimeFormat)
            if(quiz.title.isNullOrEmpty())
                quiz.title = context.getString(R.string.untitled_EN)
            if(quiz.casualDuringTime==null)
                quiz.casualDuringTime = ArrayList()
            if(quiz.duringTime==0)
                quiz.duringTime = -1
            if(quiz.members.isNullOrEmpty()) {
                quiz.members = ArrayList()
                quiz.members!!.add(Constants.userId)
            }
            if(quiz.questions==null){
                quiz.questions = ArrayList()
            }else{
                for(question in quiz.questions!!){
                    if(question.title.isNullOrEmpty())
                        question.title = context.getString(R.string.untitled_EN)
                    if(question.description.isNullOrEmpty())
                        question.description = "none"
                    if(question.options==null)
                        question.options = ArrayList()
                    if(question.answerOptions==null)
                        question.answerOptions = ArrayList()
                    if(question.bankType.isNullOrEmpty())
                        question.bankType="none"
                    if(question.questionBank.isNullOrEmpty())
                        question.questionBank="none"
                    if(question.answerDescription.isNullOrEmpty())
                        question.answerDescription="none"
                    if(question.originateFrom.isNullOrEmpty())
                        question.originateFrom = Constants.userId
                    if(question.createdDate.isEmpty())
                        question.createdDate=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    if(question.questionImage==null)
                        question.questionImage = ArrayList()
                    if(question.answerImage==null)
                        question.answerImage = ArrayList()
                    if(question.tag==null)
                        question.tag = ArrayList()
                }
            }

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizService::class.java)
            val call = api.postQuiz(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, quiz)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val postQuizResponse = gson.fromJson(
                            response.body().charStream(),
                            quizService.PostQuizResponse::class.java
                        )
                        onSuccess(postQuizResponse.quiz)
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
                quiz.startDateTime = LocalDateTime.now().format(Constants.dateTimeFormat)
            if(quiz.endDateTime.isNullOrEmpty())
                quiz.endDateTime = LocalDateTime.now().format(Constants.dateTimeFormat)
            if(quiz.title.isNullOrEmpty())
                quiz.title = context.getString(R.string.untitled_EN)
            if(quiz.casualDuringTime==null)
                quiz.casualDuringTime = ArrayList()
            if(quiz.members.isNullOrEmpty()) {
                quiz.members = ArrayList()
                quiz.members!!.add(Constants.userId)
            }

            if(quiz.questions==null) {
                quiz.questions = ArrayList()
            }else{
                for(question in quiz.questions!!){
                    if(question.title.isNullOrEmpty())
                        question.title = context.getString(R.string.untitled_EN)
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
                        question.provider=Constants.userId
                    if(question.createdDate.isNullOrEmpty())
                        question.createdDate=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
                        Log.d("now saving quiz", "put quiz")
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