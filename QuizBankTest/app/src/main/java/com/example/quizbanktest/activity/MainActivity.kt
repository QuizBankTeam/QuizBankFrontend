package com.example.quizbanktest.activity
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.RecentViewAdapter
import com.example.quizbanktest.adapters.RecommendViewAdapter
import com.example.quizbanktest.adapters.WrongViewAdapter
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel

import com.example.quizbanktest.utils.*
import com.google.android.material.navigation.NavigationView

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import com.squareup.okhttp.ResponseBody
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val SAMPLE_CROPPED_IMG_NAME = "CroppedImage.jpg"
    private var cameraPhotoUri :Uri ?=null


    private val uCropActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = UCrop.getOutput(result.data!!)
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val base64String = encodeImage(bitmap)
            var size = estimateBase64SizeFromBase64String(base64String!!)
            Log.e("ucrop size",size.toString())

            Log.e("cropResult ",uri.toString())
            // Use uri to get the cropped image
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            Log.e("cropResult","error")
            val cropError = UCrop.getError(result.data!!)
            // Handle the cropping error here
        }
    }
    val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() ){
            result->
        Log.e("gallery result status",result.resultCode.toString())
        if(result.resultCode == RESULT_OK&&result.data!=null){
            val contentURI = result.data!!.data
            try {
                val selectedImageBitmap =
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(contentURI!!))

                var base64String = encodeImage(selectedImageBitmap!!)
                ConstantsServiceFunction.scanBase64ToOcrText(base64String!!,this@MainActivity)
                var size = estimateBase64SizeFromBase64String(base64String!!)
                Log.e("openGalleryLauncher size",size.toString())
//                binding?.cameraTest!!.setImageBitmap(selectedImageBitmap) Set the selected image from GALLERY to imageView.
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result->

        if(result.resultCode == RESULT_OK){

            val thumbnail: Bitmap? = BitmapFactory.decodeStream(getContentResolver().openInputStream(cameraPhotoUri!!))
            lifecycleScope.launch{
                var returnString = saveBitmapFileForPicturesDir(thumbnail!!)
                Log.e("save pic dir",returnString)

            }
            var base64String = encodeImage(thumbnail!!)
            ConstantsServiceFunction.scanBase64ToOcrText(base64String!!,this@MainActivity)

            var size = estimateBase64SizeFromBase64String(base64String!!)


        }else if(result.resultCode == RESULT_CANCELED){
            Log.e("camera result status result cancel",result.resultCode.toString())
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecentRecyclerView(ConstantsQuestionBank.getQuestions())
        setupRecommendRecyclerView(ConstantsRecommend.getQuestions())
        setupWrongListRecyclerView(ConstantsWrong.getQuestions())
        setupActionBar()



        var nav_view : com.google.android.material.navigation.NavigationView = findViewById(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener(this)

        var bank : ImageButton = findViewById(R.id.bank)
        bank.setOnClickListener{
            ConstantsServiceFunction.getAllUserQuestionBanks(this@MainActivity)
            val intent = Intent(this,BankActivity::class.java)
            startActivity(intent)
        }

        var camera : ImageButton = findViewById(R.id.camera)
        camera?.setOnClickListener {
//
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(
                pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    // Here we have create the methods for image selection from GALLERY
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
        var settingButton : ImageButton = findViewById(R.id.setting)
        settingButton.setOnClickListener{
            ConstantsServiceFunction.login(this@MainActivity)

        }
        ConstantsServiceFunction.getCsrfToken(this@MainActivity)
        ConstantsServiceFunction.login(this@MainActivity)
    }
    private fun choosePhotoFromGallery() {

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
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    var idImage = System.currentTimeMillis()/1000
    private fun takePhotoFromCamera() {

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
                    val uri1 = FileProvider.getUriForFile(this@MainActivity, "com.example.quizbanktest.fileprovider", f)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        uri1)
                    cameraPhotoUri = uri1
                    cameraLauncher.launch(intent)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }
    fun estimateBase64SizeFromBase64String(base64String: String): Int {
        val base64Chars = base64String.length
        val originalSizeInBytes = (base64Chars * (3.0 / 4.0)).toInt()
        return (originalSizeInBytes * (4.0 / 3.0)).toInt()
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        bm.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun setupRecentRecyclerView(quizBankList: ArrayList<QuestionBankModel>) {
        var recentQuizBankList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_quiz_bank_list)
        recentQuizBankList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentQuizBankList?.setHasFixedSize(true)

        val placesAdapter = RecentViewAdapter(this, quizBankList)
        recentQuizBankList?.adapter = placesAdapter


    }

    private fun setupWrongListRecyclerView(wrongList: ArrayList<QuestionModel>) {
        var recentWrongList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_wrong_list)
        recentWrongList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentWrongList?.setHasFixedSize(true)

        val placesAdapter = WrongViewAdapter(this, wrongList)
        recentWrongList?.adapter = placesAdapter
    }
    private suspend fun saveBitmapFileForPicturesDir(mBitmap: Bitmap?): String {
        Log.e("in sava", "save")
        var result = ""
        if (mBitmap != null) {
            var base64URL = encodeImage(mBitmap)
//            if (base64URL != null) {
//                Log.e("base64URL:  ", base64URL)
//            }
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
                        } else {
                            Toast.makeText(
                                this@MainActivity,
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
    private fun setupRecommendRecyclerView(recommendList: ArrayList<QuestionModel>) {
        var recentRecommendList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recent_recommend_list)
        recentRecommendList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recentRecommendList?.setHasFixedSize(true)

        val placesAdapter = RecommendViewAdapter(this, recommendList)
        recentRecommendList?.adapter = placesAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    private fun toggleDrawer() {
        var drawer_layout :DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                Toast.makeText(this@MainActivity, "My Profile", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_sign_out -> {
               ConstantsServiceFunction.logout(this@MainActivity)
            }
            R.id.imageEditor ->{
                val intent = Intent(this,PaintActivity::class.java)
                startActivity(intent)
            }
            R.id.ocrWorkSpace ->{
                val intent = Intent(this,ScannerTextWorkSpaceActivity::class.java)
                startActivity(intent)
            }
        }
        var drawer_layout :DrawerLayout = findViewById(R.id.drawer_layout)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun setupActionBar() {
        var toolbar_main_activity : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home_detail)
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2

        private const val IMAGE_DIRECTORY = "QuizTest"

    }

}