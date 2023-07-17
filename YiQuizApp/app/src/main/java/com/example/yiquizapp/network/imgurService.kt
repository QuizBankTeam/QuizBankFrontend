package com.example.yiquizapp

import retrofit.Call
import retrofit.http.Body
import retrofit.http.POST

interface ImgurService {
    data class PostBody(val base64String: String)

    @POST("/uploadImage")
    fun postBase64(@Body body: PostBody): Call<String>
}