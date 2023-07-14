package com.example.yiquizapp.utils

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.yiquizapp.activity.ScannerTextWorkSpaceActivity
import com.example.yiquizapp.network.ScanImageService
import com.google.gson.Gson
import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit

object ConstantsScanServiceFunction {
    fun scanBase64ToOcrText(base64String: String, activity:AppCompatActivity) {
        if (Constants.isNetworkAvailable(activity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(ScanImageService::class.java)
            val body = ScanImageService.PostBody(base64String)

            //TODO 拿到csrf token access token
            Log.e("access in scan ", Constants.accessToken)
            Log.e("COOKIE in scan ", Constants.COOKIE)
            val call = api.scanBase64(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.accessToken,
                Constants.refreshToken,
                body
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val ocrResponse = gson.fromJson(
                            response.body().charStream(),
                            OCRResponse::class.java
                        )
                        Log.e("Response Result", ocrResponse.text)
                        ConstantsOcrResults.setOcrResult(ocrResponse.text)
                        val intent = Intent(activity, ScannerTextWorkSpaceActivity::class.java)
                        intent.putExtra("ocrText", ocrResponse.text)
                        activity.startActivity(intent)

                    } else {

                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e(
                                    "Error 400", "Bad Re" +
                                            "" +
                                            "quest"
                                )
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "in scan Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable?) {
                    Log.e("in scan Errorrrrr", t?.message.toString())
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
    data class OCRResponse(val text: String)

}