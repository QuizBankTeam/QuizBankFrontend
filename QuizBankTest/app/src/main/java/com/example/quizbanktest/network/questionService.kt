package com.example.quizbanktest.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.Response
import retrofit.http.*
import retrofit2.http.Headers
import java.util.*
import kotlin.collections.ArrayList

interface QuestionService {

    data class DeleteQuestionBody(val questionId: UUID)
    data class PostQuestionBody(val title: String,val number: String,val description: String,val options: ArrayList<String>,val questionType:String,val bankType:String,val questionBank:UUID,val answerOptions:ArrayList<String>,val answerDescription:String,val provider:UUID,val originateFrom:UUID,val createdDate:String,val image : String,val tag:ArrayList<String>)
    data class GetQuestionBody(val questionId:UUID)
    data class PutQuestionBody(val questionId:UUID,val title: String,val number: String,val description: String,val options: ArrayList<String>,val questionType:String,val bankType:String,val questionBank:UUID,val answerOptions:ArrayList<String>,val answerDescription:String,val provider:UUID,val originateFrom:UUID,val createdDate:String,val image : String,val tag:ArrayList<String>)

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
        @Body body: GetQuestionBody
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