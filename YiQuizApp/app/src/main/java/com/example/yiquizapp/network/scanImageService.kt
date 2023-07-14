package com.example.yiquizapp.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*
import retrofit2.http.Headers

interface ScanImageService {

    data class PostBody(val image: String)

    @POST("/scanner")
    fun scanBase64(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PostBody
    ): Call<ResponseBody>
}