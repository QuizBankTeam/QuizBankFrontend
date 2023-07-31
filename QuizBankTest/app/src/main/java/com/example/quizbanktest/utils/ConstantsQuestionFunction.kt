package com.example.quizbanktest.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.activity.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.network.QuestionBankService
import com.example.quizbanktest.network.QuestionService
import com.example.quizbanktest.network.ScanImageService
import com.google.gson.Gson
import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit

object ConstantsQuestionFunction {
    var allQuestionsReturnResponse : ConstantsQuestionFunction.AllQuestionsResponse?= null
    var questionList : ArrayList<QuestionModel> = ArrayList()
    var postQuestionPosition : Int = 0
    fun postQuestion(question : QuestionModel, activity: AppCompatActivity,onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {

        if (Constants.isNetworkAvailable(activity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(QuestionService::class.java)
//            data class PostQuestionBody(val title: String,val number: String,val description: String,val options: ArrayList<String>,val questionType:String,val bankType:String,val questionBank:String,val answerOptions:ArrayList<String>,val answerDescription:String,val provider:String,val originateFrom:String,val createdDate:String,val image : String,val tag:ArrayList<String>)

            val body = QuestionService.PostQuestionBody(question.title!!,question.number!!,question.description,question.options!!,question.questionType!!,question.bankType!!,question.questionBank!!,question.answerOptions!!,question.answerDescription!!,ConstantsAccountServiceFunction.userAccount!!._id,question.orginateFrom!!,question.createdDate!!,question.image!!,question.tag!!)

            //TODO 拿到csrf token access token
            Log.e("access in scan ", Constants.accessToken)
            Log.e("COOKIE in scan ", Constants.COOKIE)
            val call = api.postQuestion(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.accessToken,
                Constants.refreshToken,
                body
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        Log.e("Response Result","success post question")
                        Toast.makeText(activity,"upload successfully",Toast.LENGTH_SHORT).show()
                        ConstantsOcrResults.questionList.removeAt(postQuestionPosition)

                        onSuccess("upload ok")
                    } else {
                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e(
                                    "Error 400", "Bad Re" +
                                            "" +
                                            "quest"+response.toString()
                                )
                                Toast.makeText(activity,"Error 400",Toast.LENGTH_SHORT).show()
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                                Toast.makeText(activity,"Not Found 400",Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.e("Error", "in post question Error")
                                Toast.makeText(activity,"error",Toast.LENGTH_SHORT).show()
                            }
                        }
                        onFailure("bad request")
                    }
                }
                override fun onFailure(t: Throwable?) {
                    onFailure("bad request")
                    Log.e("in post question Errorrrrr", t?.message.toString())
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

    fun getQuestion(context: Context, ID: String, onSuccess: (ArrayList<QuestionModel>) -> Unit, onFailure: (String) -> Unit) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(QuestionBankService::class.java)
            //TODO 拿到csrf token access token
            Log.e("access in scan ", Constants.accessToken)
            Log.e("COOKIE in scan ", Constants.COOKIE)
            val call = api.getQuestionBankByID(
                Constants.COOKIE,
                Constants.csrfToken,
                Constants.session,
                ID
            )
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        // TODO
                        val gson = Gson()
                        val allQuestionsResponse = gson.fromJson(
                            response.body().charStream(),
                            AllQuestionsResponse::class.java
                        )
                        allQuestionsReturnResponse = allQuestionsResponse
                        questionList = allQuestionsResponse.questions
                        Log.e("Question Response Result", questionList.toString())
                        onSuccess(allQuestionsResponse.questions)
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
                    Log.e("in get all questions Error", t?.message.toString())
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

    data class AllQuestionsResponse(val questions:ArrayList<QuestionModel>)

}