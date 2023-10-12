package com.example.quizbanktest.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LatexOcrService {
    @Multipart
    @POST("/latexocr")
    fun realesrgan(
        @Header("Cookie") cookie: String,
        @Header("X-CSRF-Token") csrfToken: String,
        @Header("accessToken") accessToken: String,
        @Header("refreshToken") refreshToken: String,
        @Header("Content-Type") contentType: String,
        @Part("image") image: MultipartBody
    ): Call<ResponseBody>
}