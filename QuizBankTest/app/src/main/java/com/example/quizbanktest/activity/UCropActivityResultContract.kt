package com.example.quizbanktest.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.yalantis.ucrop.UCrop

class UCropActivityResultContract : ActivityResultContract<UCrop, Uri?>() {
    override fun createIntent(context: Context, uCrop: UCrop): Intent {
        return uCrop.getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            Log.e("cut image ", UCrop.getOutput(intent).toString())
            return UCrop.getOutput(intent)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(intent!!)
            Log.e("cut image ","null")
            // Handle the cropping error here
        }
        return null
    }
}