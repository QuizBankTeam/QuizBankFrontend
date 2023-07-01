package com.example.quizbanktest.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*
import java.util.*
import kotlin.collections.ArrayList

interface QuestionSetService {

    data class DeleteQuestionSetBody(val questionSetId: String)
    data class PostQuestionSetBody(val description: String,val image: ArrayList<String>,val questionBank:String,val questions: ArrayList<QuestionBody>,val provider: String,val createdDate:String,val originateFrom:String)
    data class GetQuestionSetBody(val questionSetId:String)
    data class PutQuestionSetBody(val questionSetId:String,val description: String,val image: ArrayList<String>,val questionBank:String,val questions: ArrayList<QuestionBody>,val provider: String,val createdDate:String,val originateFrom:String)
    data class QuestionBody(val title: String,val number: String,val description: String,val options: ArrayList<String>,val questionType:String,val bankType:String,val questionBank:String,val answerOptions:ArrayList<String>,val answerDescription:String,val provider:String,val originateFrom:String,val createdDate:String,val image : String,val tag:ArrayList<String>)

    @POST("/questionSet")
    fun postQuestionSet(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PostQuestionSetBody
    ): Call<ResponseBody>

    @GET("/questionSet")
    fun getQuestionSetByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Body body: GetQuestionSetBody
    ): Call<ResponseBody>

    @PUT("/questionSet")
    fun updateQuestionSet(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PutQuestionSetBody
    ): Call<ResponseBody>

    @DELETE("/questionSet")
    fun deleteQuestionSetByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Body body: DeleteQuestionSetBody
    ): Call<ResponseBody>

}