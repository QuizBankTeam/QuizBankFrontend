package com.example.quizbanktest.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.quizbanktest.activity.IntroActivity
import com.example.quizbanktest.network.HoughRotateService
import com.example.quizbanktest.network.RealEsrganService
import com.google.gson.Gson
//import com.squareup.okhttp.OkHttpClient
//import com.squareup.okhttp.ResponseBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
//import retrofit.Callback
//import retrofit.GsonConverterFactory
//import retrofit.Response
//import retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

object ConstantsHoughAlgo {

    fun imageRotate(base64String: String, activity: Activity, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(activity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(HoughRotateService::class.java)

            val body = HoughRotateService.PostBody(base64String)

            //TODO 拿到csrf token access token
            Log.e("access in scan ", Constants.accessToken)
            Log.e("COOKIE in scan ", Constants.COOKIE)
            val call = api.imageRotate(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.accessToken,
                Constants.refreshToken,
                body
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {

                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        val houghResponse = gson.fromJson(
                            response.body()?.charStream(),
                            HoughResponse::class.java
                        )
                        Log.e("Response Result", houghResponse.image)

                        onSuccess(houghResponse.image)

                    } else {

                        val sc = response.code()


                        when (sc) {
                            400 -> {

                                Log.e(
                                    "Error 400", "Bad Re" +
                                            "" +
                                            "quest"
                                )
                                onFailure("Request failed with status code $sc")
                            }
                            404 -> {

                                Log.e("Error 404", "Not Found")
                                onFailure("Request failed with status code $sc")
                            }
                            401 -> {
                                val intent = Intent(activity, IntroActivity::class.java)
                                activity.startActivity(intent)
                            }
                            else -> {
                                Log.e("Error", "in hough Generic Error")
                                onFailure("Request failed with status code $sc")
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    Log.e("in hough Errorrrrr", t?.message.toString())
                    onFailure("Request failed with status code")
                }
            })
        } else {

            Toast.makeText(
                activity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    data class HoughResponse(val image: String)
}