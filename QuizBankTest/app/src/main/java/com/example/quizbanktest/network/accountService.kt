package com.example.quizbanktest.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*
import retrofit2.http.Headers

interface AccountService {

    data class PostBody(val username: String,val password:String)

    @POST("/login")
    fun login(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("Session-Token")session : String,
        @Body body: PostBody
    ): Call<ResponseBody>
}