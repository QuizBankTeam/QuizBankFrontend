package com.example.quizbanktest.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Base64
import android.util.Log

import androidx.appcompat.app.AlertDialog
import com.example.quizbanktest.BuildConfig
import okhttp3.ConnectionPool
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

object ConstantsFunction {
    var idImage = System.currentTimeMillis()/1000
    private val SAMPLE_CROPPED_IMG_NAME = "CroppedImage.jpg"
    private var cameraPhotoUri : Uri ?=null

    fun estimateBase64SizeFromBase64String(base64String: String): Int {
        val base64Chars = base64String.length
        val originalSizeInBytes = (base64Chars * (3.0 / 4.0)).toInt()
        return (originalSizeInBytes * (4.0 / 3.0)).toInt()
    }

    fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    @SuppressLint("Recycle")
    fun encodeFileImage(context: Context, uri: Uri): String? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        return if (bytes != null) {
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } else {
            null
        }
    }

    fun showRationalDialogForPermissions(context: Context) {
        AlertDialog.Builder(context)
            .setMessage("目前似乎已關閉此功能所需的權限。 請在設定下啟用它")
            .setPositiveButton("前往設定"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("取消") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }
    fun createMultipartFromUri(uri: Uri, context: Context): MultipartBody.Part {

        val inputStream = context.contentResolver.openInputStream(uri)
        val contentType = context.contentResolver.getType(uri)
        val fileName = getFileNameFromUri(uri, context)
        Log.e("file name",fileName)
        val requestFile = RequestBody.create(contentType?.let { it.toMediaTypeOrNull() }, inputStream!!.readBytes())
        return MultipartBody.Part.createFormData("image", fileName, requestFile)

    }
    @SuppressLint("Range")
    fun getFileNameFromUri(uri: Uri, context: Context): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        var fileName = ""
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                if (!displayName.isNullOrEmpty()) {
                    fileName = displayName
                }
            }
        }
        return fileName
    }
    fun createOkHttpClient(): OkHttpClient {
        val logging: HttpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(if(BuildConfig.DEBUG){HttpLoggingInterceptor.Level.BODY}else
            {HttpLoggingInterceptor.Level.BODY})
        return OkHttpClient.Builder()
            .addNetworkInterceptor(logging)
            .connectTimeout(6L, TimeUnit.MINUTES)
            .readTimeout(6L, TimeUnit.MINUTES)
            .connectionPool(ConnectionPool(5, 30, TimeUnit.SECONDS))
            .retryOnConnectionFailure(true) // 啟用重試
            .build()
    }

}