package com.example.quizbanktest.utils
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

object Constants {
//
//    const val BASE_URL: String = "http://192.168.1.116:5000/"
    const val BASE_URL: String = "https://quizbank.soselab.tw/"
    const val RESULT_DELETE = 1500
    var username : String = "test"
    var password : String = "test"
    var userId: String = "791f34e7-9a6b-4406-85c4-68df9af7c182"
    var csrfToken : String = ""
    var session:String = ""
    var cookie:String=""
    var refreshToken : String = ""
    var accessToken : String = ""
    var COOKIE: String = ""
    var EXPIRE: Int = 0 //用於在每次login 後 去判斷server token是否過期
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val optionNum = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
    const val quizStatusReady = "ready"
    const val quizStatusDraft = "draft"
    const val quizStatusDoing = "doing"
    const val quizTypeSingle = "single"
    const val quizTypeCasual = "casual"
    const val questionTypeFilling = "Filling"
    const val questionTypeMultipleChoiceS = "MultipleChoiceS"
    const val questionTypeShortAnswer = "ShortAnswer"
    const val questionTypeMultipleChoiceM = "MultipleChoiceM"
    const val questionTypeTrueOrFalse = "TrueOrFalse"
    const val TrueOrFalseAnsTrue = "true"
    const val TrueOrFalseAnsFalse = "false"
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

    fun bitmapToString(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}

