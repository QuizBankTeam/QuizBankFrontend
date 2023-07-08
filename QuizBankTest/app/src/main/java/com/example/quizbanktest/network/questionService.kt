package com.example.quizbanktest.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.Response
import retrofit.http.*
import retrofit2.http.Headers
import java.util.*
import kotlin.collections.ArrayList

interface QuestionService {

    data class DeleteQuestionBody(val questionId: String)
    data class PostQuestionBody(val title: String,val number: String,val description: String,val options: ArrayList<String>,val questionType:String,val bankType:String,val questionBank:String,val answerOptions:ArrayList<String>,val answerDescription:String,val provider:String,val originateFrom:String,val createdDate:String,val image : ArrayList<String>,val tag:ArrayList<String>)
    data class GetQuestionBody(val questionId:String)
    data class PutQuestionBody(val questionId:String,val title: String,val number: String,val description: String,val options: ArrayList<String>,val questionType:String,val bankType:String,val questionBank:String,val answerOptions:ArrayList<String>,val answerDescription:String,val provider:String,val originateFrom:String,val createdDate:String,val image : String,val tag:ArrayList<String>)

    @POST("/question")
    fun postQuestion(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PostQuestionBody
    ): Call<ResponseBody>

    @GET("/question")
    fun getQuestionByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Query("questionId") questionId: String
    ): Call<ResponseBody>

    @PUT("/question")
    fun updateQuestion(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PutQuestionBody
    ): Call<ResponseBody>

    @DELETE("/question")
    fun deleteQuestionByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: DeleteQuestionBody
    ): Call<ResponseBody>

}