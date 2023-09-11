package com.example.quizbanktest.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*
import java.lang.annotation.Documented
import java.util.*
import javax.xml.transform.OutputKeys.METHOD
import kotlin.collections.ArrayList

interface QuestionBankService {


    data class PostQuestionBankBody(val title: String,val questionBankType: String,val createdDate: String,val members: ArrayList<String>,val originateFrom:String) //questionBankType 只有 'multi', 'single', 'public' 要判斷

    data class PutQuestionBankBody(val questionBankId:String,val title: String,val questionBankType: String,val createdDate: String,val members: ArrayList<String>,val originateFrom:String,val creator:String)

    @POST("/questionBank")
    fun postQuestionBank(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PostQuestionBankBody
    ): Call<ResponseBody>

    @GET("/questionBank/{questionBankId}")
    fun getQuestionBankByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Path("questionBankId") questionBankId: String
    ): Call<ResponseBody>

    @GET("/questionBanks/{bankType}")
    fun getAllQuestionBanks(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Path("bankType") bankType: String
    ): Call<ResponseBody>

    @PUT("/questionBank")
    fun updateQuestionBank(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PutQuestionBankBody
    ): Call<ResponseBody>

    @DELETE("/questionBank/{questionBankId}")
    fun deleteQuestionBankByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Path("questionBankId") questionBankId: String
    ): Call<ResponseBody>

}