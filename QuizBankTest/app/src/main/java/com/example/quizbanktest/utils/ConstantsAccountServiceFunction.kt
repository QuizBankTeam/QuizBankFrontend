package com.example.quizbanktest.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.example.quizbanktest.activity.IntroActivity
import com.example.quizbanktest.models.AccountModel
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.network.AccountService
import com.example.quizbanktest.network.CsrfTokenService
import com.google.gson.Gson
import com.squareup.okhttp.Headers
import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit

object ConstantsAccountServiceFunction {

    var userAccount : AccountModel ? = null
    fun getCsrfToken(context: Context) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(CsrfTokenService::class.java)
            //TODO 拿到csrf token
            val call = api.getCSRFToken()

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val cookies: String = response.headers().get("Set-Cookie")
                        val cookieHeader: Headers? = response.headers()
                        val cookiesToken: String? = cookieHeader?.get("Set-Cookie")
                        val cookieHeaders = response.headers().values("Set-Cookie")
                        var csrfToken: String? = null
                        var session: String? = null
                        for (cookie in cookieHeaders) {
                            if (cookie.startsWith("CSRF-TOKEN")) {
                                val parts = cookie.split(";").toTypedArray()
                                csrfToken = parts[0].substringAfter("CSRF-TOKEN=").trim()
                            }
                            if (cookie.startsWith("session")) {
                                val parts = cookie.split(";").toTypedArray()
                                session = parts[0].substringAfter("session=").trim()
                            }
                        }
                        Constants.csrfToken = csrfToken!!
                        Constants.session = session!!
                        Constants.cookie =
                            "CSRF-TOKEN=" + Constants.csrfToken + ";" + "session=" + Constants.session

                    } else {

                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e(
                                    "in csrf Error 400", "Bad Re" +
                                            "" +
                                            "quest"
                                )
                            }
                            404 -> {
                                Log.e("in csrf Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("in csrf Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable?) {
                    Log.e("in csrf Errorrrrr", t?.message.toString())
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

    fun login(context: Context, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        getCsrfToken(context)
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(AccountService::class.java)
            Constants.username = "test"
            Constants.password = "test"
            val body = AccountService.PostBody(Constants.username, Constants.password)

            //TODO 用csrf token 拿access token

            val call = api.login(Constants.cookie, Constants.csrfToken, Constants.session, body)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val cookieHeader: Headers? = response.headers()

                        val cookieHeaders = response.headers().values("Set-Cookie")
                        var accessToken: String? = null
                        var refreshToken: String? = null
                        for (cookie in cookieHeaders) {
                            if (cookie.startsWith("refresh_token_cookie")) {
                                val parts = cookie.split(";").toTypedArray()
                                refreshToken =
                                    parts[0].substringAfter("refresh_token_cookie=").trim()
                            }
                            if (cookie.startsWith("access_token_cookie")) {
                                val parts = cookie.split(";").toTypedArray()
                                accessToken = parts[0].substringAfter("access_token_cookie=").trim()
                            }
                        }
                        val gson = Gson()
                        val accountResponse = gson.fromJson(
                            response.body().charStream(),
                            LoginApiResponse::class.java
                        )
                        userAccount = accountResponse.user
                        Log.e("account",accountResponse.toString())
                        Constants.accessToken = accessToken!!
                        Constants.refreshToken = refreshToken!!
                        var cookie: String = Constants.cookie + ";"
                        Constants.COOKIE =
                            cookie + "access_token_cookie=" + Constants.accessToken + ";" + "refresh_token_cookie=" + Constants.refreshToken
                        onSuccess("Ok")

                    } else {

                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e(
                                    "in login Error 400", "Bad Re" +
                                            "" +
                                            "quest" + response.body()
                                )
                            }
                            404 -> {
                                Log.e("in login Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("in login Error", "Generic Error")
                            }
                        }
                        onFailure("Request failed with status code $sc")
                    }
                }

                override fun onFailure(t: Throwable?) {
                    Log.e("in login Errorrrrr", t?.message.toString())
                    onFailure("Request failed with status code ")
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

    fun logout(context: Context) {
        if (Constants.isNetworkAvailable(context)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(AccountService::class.java)

            //TODO 拿到csrf token access token
            Log.e("access in scan ", Constants.accessToken)
            Log.e("COOKIE in scan ", Constants.COOKIE)
            val call = api.logout(Constants.COOKIE, Constants.csrfToken, Constants.accessToken)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(response: Response<ResponseBody>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        // 清空目前記錄的登入屬性
                        Constants.cookie = ""
                        Constants.COOKIE = ""
                        Constants.csrfToken = ""
                        Constants.accessToken = ""
                        Constants.session = ""
                        Constants.refreshToken = ""
                        Constants.username = ""
                        Constants.password = ""

                        Log.e("Response Result", "log out success")
                        val intent = Intent(context, IntroActivity::class.java)
                        context.startActivity(intent)

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
                                Log.e("Error", "in log out  Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable?) {
                    Log.e("in log out Errorrrrr", t?.message.toString())
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
    data class LoginApiResponse(val message: String, val status: Int, val user: AccountModel)

}