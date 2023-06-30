package com.example.quizbanktest.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.Response
import retrofit.http.*
import retrofit2.http.Headers
import java.util.*
import kotlin.collections.ArrayList

interface QuizBankService {

    data class DeleteQuestionBankBody(val questionBankId: UUID)
    data class PostQuestionBankBody(val title: String,val questionBankType: String,val createdDate: String,val members: ArrayList<UUID>,val originateFrom:UUID,val creator:UUID) //questionBankType 只有 'multi', 'single', 'public' 要判斷
    data class GetQuestionBankBody(val questionBankId:UUID)
    data class PutQuestionBankBody(val questionBankId:UUID,val title: String,val questionBankType: String,val createdDate: String,val members: ArrayList<UUID>,val originateFrom:UUID,val creator:UUID)

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
        @Body body: GetQuestionBankBody
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