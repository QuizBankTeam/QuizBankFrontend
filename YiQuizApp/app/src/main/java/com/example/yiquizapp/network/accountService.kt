package com.example.yiquizapp.network

import com.example.yiquizapp.utils.ConstantsAccountServiceFunction
import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*
import java.util.*

interface AccountService {
    data class GetBodyForUserProfile(val userId :String)
    data class PostBody(val username: String,val password:String) //login
    data class PostBodyForRegister(val username: String,val email: String,val password:String,val createdDate: String) //login


    @POST("/login")
    fun login(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Body body: PostBody
    ): Call<ResponseBody>



    @GET("/profile")
    fun getUserProfile(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Query("userId") userId: String
    ): Call<ResponseBody>

    @POST("/register")
    fun register(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Body body: PostBodyForRegister
    ): Call<ResponseBody>

    @POST("/logout")
    fun logout(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String
    ):Call<ResponseBody>
}