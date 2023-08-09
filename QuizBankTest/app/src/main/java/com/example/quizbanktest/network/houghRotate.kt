package com.example.quizbanktest.network

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*

interface HoughRotateService {
    data class PostBody(val image: String)

    @POST("/imageRotate")
    fun imageRotate(
        @Header("Cookie") cookie:String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Body body: HoughRotateService.PostBody
    ): Call<ResponseBody>
}