package com.example.quizbanktest.network

//import com.squareup.okhttp.ResponseBody
import okhttp3.ResponseBody
//import retrofit.Call
//import retrofit.Response
//import retrofit.http.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
//import retrofit2.http.Headers
import retrofit2.http.POST

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