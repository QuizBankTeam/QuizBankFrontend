package com.example.yiquizapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.yiquizapp.models.QuestionBankModel
import com.example.yiquizapp.QuestionBankService
import com.google.gson.Gson
import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit

object ConstantsQuestionBankFunction {
    var allBanksReturnResponse : AllQuestionBanksResponse?= null
    var questionBankList : ArrayList<QuestionBankModel> = ArrayList()


    fun getAllUserQuestionBanks(context: Context, onSuccess: (ArrayList<QuestionBankModel>) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(QuestionBankService::class.java)
            //TODO 拿到csrf token access token
            Log.e("access in scan ", Constants.accessToken)
            Log.e("COOKIE in scan ", Constants.COOKIE)
            val call = api.getAllQuestionBanks(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.session,
                "single"
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val gson = Gson()
                        val allBanksResponse = gson.fromJson(
                            response.body().charStream(),
                            AllQuestionBanksResponse::class.java
                        )
                        Log.e("Response Result", allBanksResponse.questionBanks[0].toString())
                        allBanksReturnResponse = allBanksResponse
                        questionBankList = allBanksResponse.questionBanks
                        onSuccess(allBanksResponse.questionBanks)
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
                                Log.e("Error", "in get all banks Generic Error")
                            }
                        }
                        onFailure("Request failed with status code $sc")
                    }
                }

                override fun onFailure(t: Throwable?) {
                    onFailure("Request failed with status code ")
                    Log.e("in get all banks Errorrrrr", t?.message.toString())
                }
            })
        } else {
            Toast.makeText(
                context,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    data class AllQuestionBanksResponse(val questionBanks:ArrayList<QuestionBankModel>)
}