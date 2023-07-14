package com.example.yiquizapp.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*
import java.util.*
import kotlin.collections.ArrayList

interface QuestionBankService {

    data class DeleteQuestionBankBody(val questionBankId: String)
    data class PostQuestionBankBody(val title: String,val questionBankType: String,val createdDate: String,val members: ArrayList<String>,val originateFrom:String) //questionBankType 只有 'multi', 'single', 'public' 要判斷
    data class GetQuestionBankBody(val questionBankId:String)
    data class GetAllQuestionBanksBody(val bankType:String)
    data class PutQuestionBankBody(val questionBankId:String,val title: String,val questionBankType: String,val createdDate: String,val members: ArrayList<String>,val originateFrom:String,val creator:String)

    @POST("/questionBank")
    fun postQuestionBank(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PostQuestionBankBody
    ): Call<ResponseBody>

    @GET("/questionBank")
    fun getQuestionBankByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Query("questionBankId") questionBankId: String
    ): Call<ResponseBody>

    @GET("/questionBanks")
    fun getAllQuestionBanks(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Query("bankType") bankType: String
    ): Call<ResponseBody>

    @PUT("/questionBank")
    fun updateQuestionBank(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PutQuestionBankBody
    ): Call<ResponseBody>

    @DELETE("/questionBank")
    fun deleteQuestionBankByID(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Session-Token")session : String,
        @Body body: DeleteQuestionBankBody
    ): Call<ResponseBody>

}