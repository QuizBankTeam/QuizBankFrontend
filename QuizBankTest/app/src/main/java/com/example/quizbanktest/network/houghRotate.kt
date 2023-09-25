package com.example.quizbanktest.network

//import com.squareup.okhttp.ResponseBody
//import retrofit.Call
//import retrofit.http.*
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.http.Body
//import retrofit2.http.DELETE
//import retrofit2.http.GET
import retrofit2.http.Header
//import retrofit2.http.Headers
import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Path
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