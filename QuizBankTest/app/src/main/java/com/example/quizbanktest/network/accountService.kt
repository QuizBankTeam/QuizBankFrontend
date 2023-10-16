package com.example.quizbanktest.network

import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
//import com.squareup.okhttp.ResponseBody
//import retrofit.Call
//import retrofit.http.*
import java.util.*
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.http.Body
//import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
//import retrofit2.http.Headers
import retrofit2.http.POST
//import retrofit2.http.PUT
import retrofit2.http.Path
interface AccountService {
    data class GetBodyForUserProfile(val userId :String)
    data class PostBody(val username: String,val password:String) //login
    data class PostBodyForRegister(val username: String,val email: String,val password:String,val createdDate: String) //register

    data class ForgetBody(val email : String)

    @POST("/login")
    fun login(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Body body: PostBody
    ): Call<ResponseBody>

    @POST("/forgotPassword")
    fun forgotPassword(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Body body: ForgetBody
    ): Call<ResponseBody>

    @GET("/profile/{userId}")
    fun getUserProfile(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Path("userId") userId: String
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