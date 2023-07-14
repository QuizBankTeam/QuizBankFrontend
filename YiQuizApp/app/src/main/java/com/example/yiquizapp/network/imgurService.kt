package com.example.yiquizapp.network

import retrofit.Call
import retrofit.http.Body
import retrofit.http.GET
import retrofit.http.POST
import retrofit.http.Query
interface ImgurService {
    data class PostBody(val base64String: String)

    @POST("/uploadImage")
    fun postBase64(@Body body: PostBody): Call<String>
}