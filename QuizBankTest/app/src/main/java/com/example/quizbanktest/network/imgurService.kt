package com.example.quizbanktest.network

//import retrofit.Call
//import retrofit.http.Body
//import retrofit.http.GET
//import retrofit.http.POST
//import retrofit.http.Query
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.http.Body
//import retrofit2.http.DELETE
//import retrofit2.http.GET
//import retrofit2.http.Header
//import retrofit2.http.Headers
import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Path
interface ImgurService {
    data class PostBody(val base64String: String)

    @POST("/uploadImage")
    fun postBase64(@Body body: PostBody): Call<String>
}