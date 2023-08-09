package com.example.yiquizapp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import java.io.ByteArrayOutputStream

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
        bm.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun showRationalDialogForPermissions(context: Context) {
        AlertDialog.Builder(context)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"
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
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }


}