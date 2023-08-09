package com.example.yiquizapp

import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.*
import retrofit2.http.Headers

interface CsrfTokenService {

    @GET("/")
    fun getCSRFToken(): Call<ResponseBody>
}