package com.example.quizbanktest.network
//
//import com.squareup.okhttp.ResponseBody
//import retrofit.Call
//import retrofit.http.*
//import retrofit2.http.Headers
import okhttp3.ResponseBody
import retrofit2.*
//import retrofit2.http.Body
//import retrofit2.http.DELETE
import retrofit2.http.GET
//import retrofit2.http.Header
//import retrofit2.http.Headers
//import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Path
interface CsrfTokenService {

    @GET("/")
    fun getCSRFToken(): Call<ResponseBody>
}