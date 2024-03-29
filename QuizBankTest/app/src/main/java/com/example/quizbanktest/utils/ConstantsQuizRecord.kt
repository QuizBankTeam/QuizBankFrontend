package com.example.quizbanktest.utils

import android.content.Context
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.network.quizRecordService
import com.example.quizbanktest.network.quizService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//import com.squareup.okhttp.ResponseBody
//import retrofit.Callback
//import retrofit.GsonConverterFactory
//import retrofit.Response
//import retrofit.Retrofit

object ConstantsQuizRecord {
    private val onFailureStr = "network not available"
    private var quizRecordList = ArrayList<QuizRecord>()

    fun getAllQuizRecords(context: Context, quizRecordType: String,  onSuccess: (ArrayList<QuizRecord>) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizRecordService::class.java)
            val call = api.getAllQuizRecords(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, quizRecordType)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {
                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        val allQuizRecordResponse = gson.fromJson(
                            response.body()?.charStream(),
                            quizRecordService.AllQuizRecordsResponse::class.java
                        )

                        onSuccess(allQuizRecordResponse.quizRecordList)
                    }
                    else{
                        onFailure("Request failed with status code ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("Request failed with message ${t?.message.toString()}")
                }
            })
        }else{
            onFailure(onFailureStr)
        }
    }

    fun getSingleQuizRecord(context: Context, quizRecordId: String, onSuccess: (QuizRecord, ArrayList<QuestionRecord>) -> Unit, onFailure: (String) -> Unit){
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizRecordService::class.java)
            val call = api.getSingleQuizRecord(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, quizRecordId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {
                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        val quizRecordResponse = gson.fromJson(
                            response.body()?.charStream(),
                            quizRecordService.GetQuizRecordResponse::class.java
                        )
                        val tmpQuestionRecords = ArrayList<QuestionRecord>()
                        val tmpQuestionRecordIds = ArrayList<String>()

                        for(questionRecord in quizRecordResponse.quizRecord.questionRecords){
                            tmpQuestionRecords.add(questionRecord)
                            tmpQuestionRecordIds.add(questionRecord._id)
                        }
                        val tmpQuizRecord = QuizRecord(quizRecordResponse.quizRecord._id, quizRecordResponse.quizRecord.title,
                            quizRecordResponse.quizRecord.quizId, Constants.userId, quizRecordResponse.quizRecord.type,
                            quizRecordResponse.quizRecord.totalScore, quizRecordResponse.quizRecord.duringTime,
                            quizRecordResponse.quizRecord.startDateTime, quizRecordResponse.quizRecord.endDateTime,
                            quizRecordResponse.quizRecord.members, tmpQuestionRecordIds)

                        onSuccess(tmpQuizRecord, tmpQuestionRecords)
                    } else {
                        onFailure("Request failed with status code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("Request failed with message ${t?.message.toString()}")
                }

            })
        }else{
            onFailure(onFailureStr)
        }
    }

    fun postQuizRecord(context: Context, quizRecord: QuizRecord, questionRecords: ArrayList<QuestionRecord>, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val QRInPostQuizRecordList = ArrayList<quizRecordService.QuestionRecordInPostQuizRecord>()
            for(questionRecord in questionRecords){
                val tmpQuestion = questionRecord.question
                tmpQuestion.title = if(tmpQuestion.title.isNullOrEmpty()) "none" else tmpQuestion.title
                tmpQuestion.number = if(tmpQuestion.number.isNullOrEmpty()) "1" else tmpQuestion.number
                tmpQuestion.description = if(tmpQuestion.description.isNullOrEmpty()) "none" else tmpQuestion.description
                tmpQuestion.options = if (tmpQuestion.options==null) ArrayList() else tmpQuestion.options
                tmpQuestion.questionType = if(tmpQuestion.questionType.isNullOrEmpty()) "none" else tmpQuestion.questionType
                tmpQuestion.bankType = if(tmpQuestion.bankType.isNullOrEmpty()) "none" else tmpQuestion.bankType
                tmpQuestion.questionBank = if(tmpQuestion.questionBank.isNullOrEmpty()) "none" else tmpQuestion.questionBank
                tmpQuestion.answerOptions = if (tmpQuestion.answerOptions==null) ArrayList() else tmpQuestion.answerOptions
                tmpQuestion.answerDescription = if(tmpQuestion.answerDescription.isNullOrEmpty()) "none" else tmpQuestion.answerDescription
                tmpQuestion.provider = if(tmpQuestion.provider.isNullOrEmpty()) "none" else tmpQuestion.provider
                tmpQuestion.answerImage = if (tmpQuestion.answerImage==null) ArrayList() else tmpQuestion.answerImage
                tmpQuestion.questionImage = if (tmpQuestion.questionImage==null) ArrayList() else tmpQuestion.questionImage
                tmpQuestion.tag = if (tmpQuestion.tag==null) ArrayList() else tmpQuestion.tag
                val tmpQuestionInPostQuizRecord = quizRecordService.QuestionInPostQuizRecord(tmpQuestion.title!!, tmpQuestion.number!!, tmpQuestion.description!!,
                    tmpQuestion.options!!, tmpQuestion.questionType!!, tmpQuestion.bankType!!, tmpQuestion.questionBank!!, tmpQuestion.answerOptions!!, tmpQuestion.answerDescription!!,
                    tmpQuestion.provider!!, tmpQuestion.originateFrom!!, tmpQuestion.createdDate!!, tmpQuestion.answerImage!!, tmpQuestion.questionImage!!, tmpQuestion.tag!!)

                questionRecord.userAnswerOptions = if(questionRecord.userAnswerOptions==null) ArrayList() else questionRecord.userAnswerOptions
                questionRecord.userAnswerDescription = questionRecord.userAnswerDescription.ifEmpty { "none" }
                val tmpQRInPostQuizRecord = quizRecordService.QuestionRecordInPostQuizRecord(questionRecord.userAnswerOptions!!, questionRecord.userAnswerDescription,
                questionRecord.correct!!, tmpQuestionInPostQuizRecord)
                QRInPostQuizRecordList.add(tmpQRInPostQuizRecord)
            }
            quizRecord.totalScore = if(quizRecord.totalScore==0) -1 else quizRecord.totalScore
            val postQuizRecordModel = quizRecordService.PostQuizRecord(quizRecord.title, quizRecord.quizId, Constants.userId, quizRecord.type, quizRecord.totalScore, quizRecord.duringTime!!, quizRecord.startDateTime, quizRecord.endDateTime, quizRecord.members, QRInPostQuizRecordList)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizRecordService::class.java)
            val call = api.postQuizRecord(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, postQuizRecordModel)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {
                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        val postQuizRecordResponse = gson.fromJson(
                            response.body()?.charStream(),
                            quizRecordService.PostQuizRecordResponse::class.java
                        )

                        onSuccess(postQuizRecordResponse.message)
                    }
                    else{
                        onFailure("Request failed with status code ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("Request failed with message ${t?.message.toString()}")
                }
            })
        }else{
            onFailure(onFailureStr)
        }
    }

    fun deleteQuizRecord(context: Context, quizRecordId: String,  onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(quizRecordService::class.java)
            val call = api.deleteQuizRecord(Constants.COOKIE, Constants.csrfToken, Constants.accessToken, Constants.refreshToken, Constants.session, quizRecordId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {
                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        val deleteQuizRecordResponse = gson.fromJson(
                            response.body()?.charStream(),
                            quizRecordService.DeleteQuizRecordResponse::class.java
                        )

                        onSuccess(deleteQuizRecordResponse.message)
                    }
                    else{
                        onFailure("Request failed with status code ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("Request failed with message ${t?.message.toString()}")
                }
            })
        }else{
            onFailure(onFailureStr)
        }
    }
}