package com.example.quizbanktest.utils

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.quizbanktest.activity.IntroActivity
import com.example.quizbanktest.network.LatexOcrService
import com.example.quizbanktest.network.RealEsrganService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConstantsLatexOcr {
    fun latexOcr(
        uri: Uri,
        activity: Activity,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (Constants.isNetworkAvailable(activity)) {

            val imagePart = ConstantsFunction.createMultipartFromUri(uri, activity)

            val client = ConstantsFunction.createOkHttpClient()
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val api = retrofit.create(LatexOcrService::class.java)
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
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        onSuccess(response.body().toString())

                    } else {

                        val sc = response.code()
                        when (sc) {
                            400 -> {

                                Log.e(
                                    "Error 400", response.message() + response.body()?.string()
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
                                Log.e("Error", "$sc")
                                onFailure(sc.toString())
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    Log.e("in latexOcr Errorrrrr", t?.message.toString())
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
    fun escapeSpecialCharacters(input: String): String {
        // 先轉換 \\ 至 \\\\，再轉換 \ 至 \\，然後轉換 & 至 &amp;
        return input.replace("&", "&amp;")
    }
    fun unprocessLatex(input: String): String {
        // 移除 `\\(` 和 `\\)`
        var result = input.trim()
        if (result.startsWith("\\(") && result.endsWith("\\)")) {
            result = result.drop(2).dropLast(2)
        }

        // 還原 &amp; 至 &
        result = result.replace("&amp;", "&")

        return result
    }
}