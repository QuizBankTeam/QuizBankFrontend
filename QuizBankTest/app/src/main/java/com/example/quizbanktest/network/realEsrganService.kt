package com.example.quizbanktest.network


import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.Response
import retrofit.http.*
import retrofit2.http.Headers

interface RealEsrganService {

    data class PostBody(val image: String)

    @Streaming
    @POST("/realesrgan")
    fun realesrgan(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: PostBody
    ): Call<ResponseBody>

}