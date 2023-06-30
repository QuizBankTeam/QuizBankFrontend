package com.example.quizbanktest.utils
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants {

    const val BASE_URL: String = "http://10.0.2.2:5000/"
    //const val BASE_URL: String = "http://10.0.0.108:5000/"

    var username : String = "test"//log out 記得清空
    var password : String = "test"//log out 記得清空

    var csrfToken : String = ""//log out 記得清空
    var session:String = ""//log out 記得清空
    var cookie:String=""//log out 記得清空
    var refreshToken : String = ""//log out 記得清空
    var accessToken : String = ""//log out 記得清空
    var COOKIE: String = "" //log out 記得清空
    var EXPIRE: Int = 0 //用於在每次login 後 去判斷server token是否過期


    fun isNetworkAvailable(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //比較新的系統
            val network      = connectivityManager.activeNetwork ?: return false
            val activeNetWork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                activeNetWork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else { //舊版的
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }
}