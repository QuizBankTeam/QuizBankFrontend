package com.example.quizbanktest.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.quizbanktest.network.HoughRotateService
import com.example.quizbanktest.network.RealEsrganService
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.ResponseBody
import okio.Buffer
import okio.BufferedSource
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit
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
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
//                    val source: BufferedSource? = response?.body()?.source()
//                    source?.request(Long.MAX_VALUE) // Buffer the entire body.
//
//                    val buffer: Buffer? = source?.buffer()
//                    val UTF8: Charset = Charset.forName("UTF-8")
//                    buffer?.clone()?.readString(UTF8)?.let { Log.d("REQUEST_JSON", it) }
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val houghResponse = gson.fromJson(
                            response.body().charStream(),
                            HoughResponse::class.java
                        )
                        Log.e("Response Result", houghResponse.base64)

                        onSuccess(houghResponse.base64)

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
                            else -> {
                                Log.e("Error", "in scan Generic Error")
                                onFailure("Request failed with status code $sc")
                            }

                        }
                    }
                }

                override fun onFailure(t: Throwable?) {

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
    data class HoughResponse(val base64: String)
}