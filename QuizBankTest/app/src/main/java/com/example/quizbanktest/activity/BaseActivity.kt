package com.example.quizbanktest.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore

import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.BuildCompat
import androidx.core.view.GravityCompat

import androidx.lifecycle.lifecycleScope
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.account.AccountSettingActivity
import com.example.quizbanktest.activity.bank.BankActivity
import com.example.quizbanktest.activity.quiz.QuizPage
import com.example.quizbanktest.activity.scan.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.network.socket.SocketApplication

import com.example.quizbanktest.utils.*
import com.google.android.material.snackbar.Snackbar

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.socket.client.Socket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

open class BaseActivity : AppCompatActivity() {

    private val SAMPLE_CROPPED_IMG_NAME = "CroppedImage.jpg"
    private var onImageSelected: ((Bitmap?) -> Unit)? = null
    private var cameraPhotoUri : Uri ?=null
    private lateinit var mProgressDialog: Dialog
    private lateinit var heartbeatScheduler: ScheduledExecutorService
    private var heartbeatFuture: ScheduledFuture<*>? = null

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
            showProgressDialog("目前正在處理OCR之結果")
            ConstantsScanServiceFunction.scanBase64ToOcrText(base64String!!,this@BaseActivity,1,onSuccess = { it1 ->}, onFailure = { it1 ->})

            var size = ConstantsFunction.estimateBase64SizeFromBase64String(base64String!!)


        }else if(result.resultCode == RESULT_CANCELED){
            Log.e("camera result status result cancel",result.resultCode.toString())
        }

    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.findViewById<TextView>(R.id.tv_progressbar_text).text=text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.red
            )
        )
        snackBar.show()
    }
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
    suspend fun saveBitmapFileForPicturesDir(mBitmap: Bitmap?): String {
        showProgressDialog("目前正在儲存圖片請稍等")
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
                            hideProgressDialog()
                        } else {
                            Toast.makeText(
                                this@BaseActivity,
                                "Something went wrong while saving the file.",
                                Toast.LENGTH_SHORT
                            ).show()
                            hideProgressDialog()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                    hideProgressDialog()
                }
            }
        }
        return result
    }



    var idImage = System.currentTimeMillis()/1000

    fun cameraPick(){
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems =
            arrayOf("從相簿選擇", "拍照新增題目")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                // Here we have create the methods for image selection from GALLERY
                0 -> choosePhotoToOcr()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }


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
    fun showAlertFromWorkSpace(onConfirm : () -> Unit) {
        if (ConstantsOcrResults.questionList.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
                .setMessage(" 您確定要離開嗎系統不會保存這次修改喔 ")
                .setTitle("OCR結果")
                .setIcon(R.drawable.baseline_warning_amber_24)
            builder.setPositiveButton("確認") { _, _ -> onConfirm.invoke() }
            builder.setNegativeButton("取消", null)
            builder.show()
        } else {
            onConfirm.invoke()
        }
    }

    fun gotoBankActivity(){
        ConstantsQuestionBankFunction.getAllUserQuestionBanks(this,
            onSuccess = { questionBanks ->
                when (this) {
                    is ScannerTextWorkSpaceActivity -> {
                        showAlertFromWorkSpace {
                            val intent = Intent(this, BankActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }else ->{
                        val intent = Intent(this, BankActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            },
            onFailure = { errorMessage ->
                Toast.makeText(this,"server error",Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun gotoHomeActivity(){
        when (this) {
            is ScannerTextWorkSpaceActivity -> {
                showAlertFromWorkSpace {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }else ->{
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    fun gotoQuizActivity(){
        when (this) {
            is ScannerTextWorkSpaceActivity -> {
                showAlertFromWorkSpace {
                    val intent = Intent(this, QuizPage::class.java)
                    startActivity(intent)
                    finish()
                }
            }else ->{
            val intent = Intent(this,QuizPage::class.java)
            startActivity(intent)
            finish()
            }
        }

    }

    fun gotoSettingActivity(){
        when (this) {
            is ScannerTextWorkSpaceActivity -> {
                showAlertFromWorkSpace {
                    val intent = Intent(this, AccountSettingActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }else ->{
            val intent = Intent(this,AccountSettingActivity::class.java)
            startActivity(intent)
            finish()
        }
        }

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
                showProgressDialog("目前正在處理OCR之結果")
                ConstantsHoughAlgo.imageRotate(base64String!!,this@BaseActivity,
                    onSuccess = { it1 ->
                        ConstantsScanServiceFunction.scanBase64ToOcrText(it1, this@BaseActivity,1, onSuccess = { it1 ->  }, onFailure = { it1 ->})
                }, onFailure = { it1 ->
                        ConstantsScanServiceFunction.scanBase64ToOcrText(base64String!!, this@BaseActivity,1, onSuccess = { it1 ->  }, onFailure = { it1 ->})
                } )
//                ConstantsScanServiceFunction.scanBase64ToOcrText(base64String!!, this@BaseActivity,1, onSuccess = { it1 ->  }, onFailure = { it1 ->})
                var size = ConstantsFunction.estimateBase64SizeFromBase64String(base64String!!)
//                Log.e("openGalleryLauncher size", size.toString()),1
            }else{
                Toast.makeText(this@BaseActivity,"You can't choose empty photo to ocr",Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun setupNavigationView() {
        val quiz : ImageButton = findViewById(R.id.test)
        quiz.setOnClickListener {
            gotoQuizActivity()
        }
        val bank : ImageButton = findViewById(R.id.bank)
        bank.setOnClickListener{
            gotoBankActivity()
        }

        val homeButton : ImageButton = findViewById(R.id.home)
        homeButton.setOnClickListener{
            gotoHomeActivity()
        }

        val camera : ImageButton = findViewById(R.id.camera)
        camera.setOnClickListener {
            cameraPick()
        }

        val settingButton : ImageButton = findViewById(R.id.setting)
        settingButton.setOnClickListener{
            gotoSettingActivity()
        }

    }

    private var backPressedTime: Long = 0
    private val BACK_PRESS_THRESHOLD = 2000  // 2000 milliseconds = 2 seconds
    @SuppressLint("UnsafeOptInUsageError")
    fun doubleCheckExit(){
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                doubleBackToExit()
            }
        }
    }

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > BACK_PRESS_THRESHOLD) {
            Toast.makeText(this, "再按一次返回鍵退出", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        } else {
            moveTaskToBack(true)
        }
    }

    fun splitQuestionOptions(text: String): Pair<String, List<String>> {
        val pattern = "\\s*(?:[A-Za-z0-9一二三四五六七八九十]+\\)|[A-Za-z0-9一二三四五六七八九十]+\\.)".toRegex()
        val optionIndices = pattern.findAll(text).map { it.range.first }.toList()

        if (optionIndices.isEmpty()) {
            return Pair(text, emptyList())
        }


        val question = text.substring(0, optionIndices[0]).trim()
        val options = optionIndices.mapIndexed { index, start ->
            val end = optionIndices.getOrNull(index + 1) ?: text.length
            text.substring(start, end).trim()
        }
        for(i in options){
            Log.e("list options",i)
        }

        return Pair(question, options)
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > BACK_PRESS_THRESHOLD) {
            // If back is pressed beyond the threshold time (2 seconds), show a Toast
            Toast.makeText(this, "再按一次返回鍵退出", Toast.LENGTH_SHORT).show()
            backPressedTime = currentTime
        } else {
            moveTaskToBack(true)
            // If back is pressed within the threshold time, finish the activity
//            super.onBackPressed()
        }
    }

    fun startHeartbeatForCsrf() {
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor()
        heartbeatFuture = heartbeatScheduler.scheduleAtFixedRate({
            ConstantsAccountServiceFunction.getCsrfToken(this,
                onSuccess = { it1 ->
                    Log.e("this is heart for csrf",it1)
                    ConstantsAccountServiceFunction.login(this, " ", " ",
                        onSuccess = {   message->
                            Log.d("this is heart for login ", message)
                        },
                        onFailure = { message->
                            Log.d("this is heart for login ", message)
                        })
                },
                onFailure = { it1 ->
                    Log.d("get csrf fail", it1)
                })
        }, 0, 3300, TimeUnit.SECONDS)
    }

    fun stopHeartbeatForCsrf() {
        heartbeatFuture?.cancel(false)
        heartbeatScheduler.shutdown()
    }
}