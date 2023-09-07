package com.example.quizbanktest.utils

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.example.quizbanktest.activity.BaseActivity


object ConstantsDialogFunction {

    fun dialogChoosePhotoFromGallery(activity: BaseActivity, onPhotoSelected: (Bitmap?) -> Unit) {
        activity.showProgressDialog("開啟相簿中")
        activity.choosePhotoFromGallery { bitmap ->
            if (bitmap != null) {
                onPhotoSelected(bitmap)
                activity.hideProgressDialog()
            } else {
                onPhotoSelected(null)
                Toast.makeText(activity, "You didn't choose any photo", Toast.LENGTH_SHORT).show()
                activity.hideProgressDialog()
            }
            activity.hideProgressDialog()
        }
    }


}