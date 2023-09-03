package com.example.quizbanktest.network

import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.models.QuizRecord
import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.Body
import retrofit.http.DELETE
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.POST
import retrofit.http.Query

interface quizRecordService {

    @GET("/allQuizRecords")
    fun getAllQuizRecords(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Query("quizRecordType") quizRecordType: String
    ): Call<ResponseBody>

    @GET("/quizRecord")
    fun getSingleQuizRecord(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Query("quizRecordId") quizRecordId: String
    ): Call<ResponseBody>

    @POST("/quizRecord")
    fun postQuizRecord(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Body body: PostQuizRecord
    ): Call<ResponseBody>

    @DELETE("/quizRecord")
    fun deleteQuizRecord(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Query("quizRecordId") quizRecordId: String
    ): Call<ResponseBody>
    data class QuestionInPostQuizRecord(val title: String, val number: String, val description: String,
                                 val options: ArrayList<String>, val questionType: String, val bankType: String,
                                 val questionBank: String, val answerOptions: ArrayList<String>,
                                 val answerDescription: String,  val provider: String, val originateFrom: String,
                                 val createdDate: String, val answerImage: ArrayList<String>,
                                 val questionImage: ArrayList<String>, val tag: ArrayList<String>)
    data class QuestionRecordInPostQuizRecord(val user: String, val userAnswerOptions: ArrayList<String>, val userAnswerDescription: String,
                                              val correct: Boolean, val date: String, val question: QuestionInPostQuizRecord)
    data class PostQuizRecord(val title: String, val quizId: String, val type: String,
                                  val totalScore: Int, val duringTime: Int, val startDateTime: String, val endDateTime: String,
                                  val members: ArrayList<String>, val questionRecords: ArrayList<QuestionRecordInPostQuizRecord>)
    data class AllQuizRecordsResponse(val message: String, val quizRecordList: ArrayList<QuizRecord>, val status: String)
    data class GetQuizRecordResponse(val message: String, val quizRecord: QuizRecordWithQuestionRecords, val status: String)

    data class PostQuizRecordResponse(val message: String, val status: String)

    data class DeleteQuizRecordResponse(val message: String, val status: String)

    data class QuizRecordWithQuestionRecords(val _id: String,
                                             val title: String,
                                             val quizId: String,
                                             val type: String,
                                             val totalScore:Int,
                                             val duringTime: Int?,
                                             val startDateTime: String,
                                             val endDateTime: String,
                                             val members: ArrayList<String>,
                                             val questionRecords: ArrayList<QuestionRecord>)
}