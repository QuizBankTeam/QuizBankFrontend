package com.example.quizbanktest.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

import androidx.lifecycle.lifecycleScope

import com.example.quizbanktest.utils.*

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
import java.io.IOException

open class BaseActivity : AppCompatActivity() {

    private val SAMPLE_CROPPED_IMG_NAME = "CroppedImage.jpg"
    private var onImageSelected: ((Bitmap?) -> Unit)? = null
    private var cameraPhotoUri : Uri ?=null

    val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() ){
            result->
        Log.e("gallery result status",result.resultCode.toString())
        Toast.makeText(this, "gallery!", Toast.LENGTH_SHORT).show()

        if(result.resultCode == RESULT_OK &&result.data!=null){

            val contentURI = result.data!!.data
            try {
                val selectedImageBitmap =
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(contentURI!!))
                if(selectedImageBitmap!=null){
                    onImageSelected?.invoke(selectedImageBitmap)
                }


            } catch (e: IOException) {
                onImageSelected?.invoke(null)
                e.printStackTrace()
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val cameraLauncher: ActivityResultLauncher<Intent> =registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result->
        if(result.resultCode == RESULT_OK){
            val thumbnail: Bitmap? = BitmapFactory.decodeStream(getContentResolver().openInputStream(cameraPhotoUri!!))
            lifecycleScope.launch{
                var returnString = saveBitmapFileForPicturesDir(thumbnail!!)
                Log.e("save pic dir",returnString)

            }
            var base64String = ConstantsFunction.encodeImage(thumbnail!!)
            ConstantsScanFunction.scanBase64ToOcrText(base64String!!,this@BaseActivity)

            var size = ConstantsFunction.estimateBase64SizeFromBase64String(base64String!!)


        }else if(result.resultCode == RESULT_CANCELED){
            Log.e("camera result status result cancel",result.resultCode.toString())
        }

    }


    suspend fun saveBitmapFileForPicturesDir(mBitmap: Bitmap?): String {
        Log.e("in sava", "save")
        var result = ""
        if (mBitmap != null) {
            var base64URL = ConstantsFunction.encodeImage(mBitmap)
        }
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {
                try {
                    val fileName = "QuizBank_${idImage}.jpg"
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }
                    }

                    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    contentResolver.openOutputStream(uri!!).use { outputStream ->
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    result = uri.toString()

                    runOnUiThread {
                        if (!result.isEmpty()) {
                            Toast.makeText(
                                this@BaseActivity,
                                "success scan",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@BaseActivity,
                                "Something went wrong while saving the file.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }



    var idImage = System.currentTimeMillis()/1000

    fun takePhotoFromCamera() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val f =
                        File(externalCacheDir?.absoluteFile.toString() + File.separator + "QuizBank_Camera_" + idImage + ".jpg")
                    val uri1 = FileProvider.getUriForFile(this@BaseActivity, "com.example.quizbanktest.fileprovider", f)
                    intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        uri1)
                    cameraPhotoUri = uri1
                    cameraLauncher.launch(intent)
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    ConstantsFunction.showRationalDialogForPermissions(this@BaseActivity)
                }
            }).onSameThread()
            .check()
    }

    fun gotoBankActivity(){
        ConstantsQuestionBankFunction.getAllUserQuestionBanks(this)
        val intent = Intent(this,BankActivity::class.java)
        startActivity(intent)
    }

    fun gotoHomeActivity(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    fun choosePhotoFromGallery(onImageSelected: (Bitmap?) -> Unit) {
        this.onImageSelected = onImageSelected
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    openGalleryLauncher.launch(galleryIntent)
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    ConstantsFunction.showRationalDialogForPermissions(this@BaseActivity)
                }
            }).onSameThread()
            .check()
    }
    fun  choosePhotoToOcr(){

        choosePhotoFromGallery {
            bitmap ->
            if (bitmap != null) {

                var base64String = ConstantsFunction.encodeImage(bitmap)
                ConstantsScanFunction.scanBase64ToOcrText(base64String!!, this@BaseActivity)
                var size = ConstantsFunction.estimateBase64SizeFromBase64String(base64String!!)
//                Log.e("openGalleryLauncher size", size.toString())
            }else{
                Toast.makeText(this@BaseActivity,"You can't choose empty photo to ocr",Toast.LENGTH_SHORT).show()
            }
        }

    }

}