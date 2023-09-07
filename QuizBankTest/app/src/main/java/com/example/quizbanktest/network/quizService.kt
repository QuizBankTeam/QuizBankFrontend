package com.example.quizbanktest.network

import com.example.quizbanktest.models.Quiz
import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.Body
import retrofit.http.DELETE
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.POST
import retrofit.http.PUT
import retrofit.http.Path
import retrofit.http.Query

interface quizService {

    @GET("/allQuizs")
    fun getAllQuizsWithBatch(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Query("quizType") quizType: String,
        @Query("batch") batch: Int
    ): Call<ResponseBody>

    @GET("/quiz")
    fun getSingleQuiz(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Query("quizId") quizId: String
    ): Call<ResponseBody>

    @POST("/quiz")
    fun postQuiz(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Body body: PostQuiz
    ): Call<ResponseBody>

    @PUT("/quiz")
    fun putQuiz(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Body body: PutQuiz
    ): Call<ResponseBody>

    @DELETE("/quiz")
    fun deleteQuiz(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Query("quizId") quizId: String
    ): Call<ResponseBody>

    data class QuestionInPostQuiz(
        var title: String?, val number: String, var description: String?,
        var options: ArrayList<String>?, val questionType: String, var bankType: String?,
        var questionBank: String?, var answerOptions: ArrayList<String>?,
        var answerDescription: String?, var originateFrom: String?,
        var createdDate: String, var answerImage: ArrayList<String>?,
        var questionImage: ArrayList<String>?, var tag: ArrayList<String>?)
    data class QuestionInPutQuiz(val questionId: String, val title: String, val number: String, val description: String,
                                 val options: ArrayList<String>, val questionType: String, val bankType: String,
                                 val questionBank: String, val answerOptions: ArrayList<String>,
                                 val answerDescription: String,  val provider: String, val originateFrom: String,
                                 val createdDate: String, val answerImage: ArrayList<String>,
                                 val questionImage: ArrayList<String>, val tag: ArrayList<String>)
    data class PostQuiz(var title: String?, val type: String, val status: String?, var duringTime: Int, var casualDuringTime: ArrayList<Int>?,
                        var startDateTime: String?, var endDateTime: String?, var members: ArrayList<String>?,
                        var questions: ArrayList<QuestionInPostQuiz>? )
    data class PutQuiz(val quizId: String, val title: String, val status: String, val duringTime: Int, val casualDuringTime: ArrayList<Int>,
                        val startDateTime: String, val endDateTime: String, val members: ArrayList<String>,
                        val questions: ArrayList<QuestionInPutQuiz> )
    data class AllQuizsResponse(val message: String, val quizList: ArrayList<Quiz>, val status: String)
    data class PostQuizResponse(val message: String, val quiz: Quiz, val status: String)
    data class PutQuizResponse(val message: String, val status: String)
    data class GetQuizResponse(val message: String, val quiz: Quiz, val status: String)
    data class DeleteQuizResponse(val message: String, val status: String)

}