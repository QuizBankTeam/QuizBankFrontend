package com.example.yiquizapp.utils

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.example.yiquizapp.activity.BaseActivity


object ConstantsDialogFunction {

    fun dialogChoosePhotoFromGallery(activity: BaseActivity, onPhotoSelected: (Bitmap?) -> Unit) {
        activity.choosePhotoFromGallery { bitmap ->
            if (bitmap != null) {
                onPhotoSelected(bitmap)
            } else {
                onPhotoSelected(null)
                Toast.makeText(activity, "You didn't choose any photo", Toast.LENGTH_SHORT).show()
            }
        }
    }


}