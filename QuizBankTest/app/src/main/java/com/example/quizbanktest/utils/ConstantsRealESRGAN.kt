package com.example.quizbanktest.utils

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.quizbanktest.activity.IntroActivity
import com.example.quizbanktest.network.RealEsrganService
import com.example.quizbanktest.utils.ConstantsFunction.createMultipartFromUri
import com.example.quizbanktest.utils.ConstantsFunction.createOkHttpClient
import com.example.quizbanktest.utils.ConstantsFunction.encodeImage
import com.google.gson.Gson
import okhttp3.Interceptor
//import com.squareup.okhttp.OkHttpClient
//import com.squareup.okhttp.ResponseBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
//import retrofit.Callback
//import retrofit.GsonConverterFactory
//import retrofit.Response
//import retrofit.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


object ConstantsRealESRGAN {
    fun realEsrgan(uri: Uri, activity: Activity, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(activity)) {

            val imagePart = createMultipartFromUri(uri, activity)

            val client = createOkHttpClient()
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val api = retrofit.create(RealEsrganService::class.java)
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(imagePart)
                .build()
            val contentType =
                "multipart/form-data; charset=utf-8; boundary=" + multipartBody.boundary
//            val multipartBodyString = multipartBodyToString(multipartBody )
//
//            Log.e("MultipartBody", multipartBodyString)

            val call = api.realesrgan(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.accessToken,
                Constants.refreshToken,
                contentType,
                multipartBody
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {

                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                        encodeImage(bitmap)?.let { onSuccess(it) }

                    } else {

                        val sc = response.code()
                        when (sc) {
                            400 -> {

                                Log.e(
                                    "Error 400", response.message()+response.body()?.string()
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
                                Log.e("Error","$sc")
                                onFailure(sc.toString())
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    Log.e("in esrgan Errorrrrr", t?.message.toString())
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
    fun multipartBodyToString(multipartBody: MultipartBody): String {
        val builder = StringBuilder()
        for (part in multipartBody.parts) {
            builder.append(part.headers)
            builder.append("\n")
            builder.append(bodyToString(part.body))
            builder.append("\n")
        }
        return builder.toString()
    }
    fun bodyToString(requestBody: RequestBody?): String {
        try {
            val buffer = Buffer()
            requestBody?.writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "Error reading request body"
        }
    }
    data class EsrganResponse(val image: String)

}