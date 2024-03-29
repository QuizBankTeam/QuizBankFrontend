package com.example.quizbanktest.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.activity.IntroActivity
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.network.QuestionBankService
import com.example.quizbanktest.network.QuestionService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//import com.squareup.okhttp.ResponseBody
//import retrofit.Callback
//import retrofit.GsonConverterFactory
//import retrofit.Response
//import retrofit.Retrofit

object ConstantsQuestionBankFunction {
    var allBanksReturnResponse: AllQuestionBanksResponse? = null
    var questionBankList: ArrayList<QuestionBankModel> = ArrayList()

    fun getAllUserQuestionBanks(context: Context, onSuccess: (ArrayList<QuestionBankModel>) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ConstantsFunction.createOkHttpClient())
                .build()
            val api = retrofit.create(QuestionBankService::class.java)
            //TODO 拿到csrf token access token
//            Log.e("access in scan ", Constants.accessToken)
//            Log.e("COOKIE in scan ", Constants.COOKIE)
            val call = api.getAllQuestionBanks(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.session,
                "single"
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {
                    if (response!!.isSuccessful) {
                        val gson = Gson()
                        val allBanksResponse = gson.fromJson(
                            response.body()?.charStream(),
                            AllQuestionBanksResponse::class.java
                        )
//                        Log.e("Response Result", allBanksResponse.questionBanks[0].toString())
                        allBanksReturnResponse = allBanksResponse
                        questionBankList.clear()
                        questionBankList = allBanksResponse.questionBanks
                        if (allBanksResponse.questionBanks.size == 0) {
                            onFailure("Request failed with status code ")
                        } else {
                            onSuccess(allBanksResponse.questionBanks)
                        }

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
                                onFailure("empty")
                            }
                            401 -> {
                                val intent = Intent(context, IntroActivity::class.java)
                                context.startActivity(intent)
                            }
                            else -> {
                                Log.e("Error", "in get all banks Generic Error")
                                onFailure("Request failed with status code $sc")
                            }
                        }
                        onFailure("Request failed with status code $sc")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("Request failed with status code ")
                    Log.e("in get all banks error", t?.message.toString())
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

    fun postQuestionBank(questionBank: QuestionBankModel, activity: AppCompatActivity, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(activity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(QuestionBankService::class.java)
            val body = QuestionBankService.PostQuestionBankBody(
                questionBank.title,
                questionBank.questionBankType,
                questionBank.createdDate,
                questionBank.members,
                questionBank.originateFrom
            )
            val call = api.postQuestionBank(
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
                        Log.e("Response Result", "success post bank")
                        Toast.makeText(activity, "upload successfully", Toast.LENGTH_SHORT).show()

                        onSuccess("upload successfully")
                    } else {
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad Request: $response")
                                Toast.makeText(activity, "Error 400", Toast.LENGTH_SHORT).show()
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                                Toast.makeText(activity, "Not Found 400", Toast.LENGTH_SHORT).show()
                            }
                            401 -> {
                                val intent = Intent(activity, IntroActivity::class.java)
                                activity.startActivity(intent)
                            }
                            else -> {
                                Log.e("Error", "in post question Error")
                                Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
                            }
                        }
                        onFailure("bad request")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("bad request")
                    Log.e("in post bank error", t?.message.toString())
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

    fun putQuestionBank(activity: AppCompatActivity, questionBank: QuestionBankModel, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(activity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(QuestionBankService::class.java)
            val body = QuestionBankService.PutQuestionBankBody(
                questionBank._id,
                questionBank.title,
                questionBank.questionBankType,
                questionBank.members,
            )
            val call = api.updateQuestionBank(
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
                        Toast.makeText(activity, "put successfully", Toast.LENGTH_SHORT).show()

                        onSuccess("put successfully")
                    } else {
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad Request$response")
                                Toast.makeText(activity, "Error 400", Toast.LENGTH_SHORT).show()
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                                Toast.makeText(activity, "Not Found 400", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.e("Error", "in put question Error")
                                Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
                            }
                        }
                        onFailure("put unsuccessfully")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("bad request")
                    Log.e("in put bank error", t?.message.toString())
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

    fun deleteQuestionBank(activity: AppCompatActivity, questionBankId: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(activity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(QuestionBankService::class.java)

            val call = api.deleteQuestionBankByID(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.accessToken,
                Constants.refreshToken,
                Constants.session,
                questionBankId
            )
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>) {
                    if (response!!.isSuccessful) {
                        Toast.makeText(activity, "delete successfully", Toast.LENGTH_SHORT).show()

                        onSuccess("delete successfully")
                    } else {
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad Request$response")
                                Toast.makeText(activity, "Error 400", Toast.LENGTH_SHORT).show()
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                                Toast.makeText(activity, "Not Found 400", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.e("Error", "in post question Error")
                                Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
                            }
                        }
                        onFailure("delete unsuccessfully")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure("bad request")
                    Log.e("in delete bank error", t?.message.toString())
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

    data class AllQuestionBanksResponse(val questionBanks: ArrayList<QuestionBankModel>)
}