package com.example.quizbanktest.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.BankRecyclerViewAdapter
import com.example.quizbanktest.adapters.RecentViewAdapter
import com.example.quizbanktest.adapters.WrongViewAdapter
import com.example.quizbanktest.models.BankModel
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.network.ImgurService
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.view.WrapLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import jp.wasabeef.blurry.Blurry
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

class BankActivity : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var menuButton: ImageButton
    private var wrapLayout: WrapLayout? = null
    private var blurred = false
    private val SAMPLE_CROPPED_IMG_NAME = "CroppedImage.jpg"
    private var cameraPhotoUri :Uri ?=null
    var bankModels = ArrayList<BankModel>()
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
                var size = estimateBase64SizeFromBase64String(base64String!!)
                Log.e("openGalleryLauncher size",size.toString())
//                binding?.cameraTest!!.setImageBitmap(selectedImageBitmap) Set the selected image from GALLERY to imageView.
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@BankActivity, "Failed!", Toast.LENGTH_SHORT).show()
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

//            uploadImageToImgur(base64String!!)
            var size = estimateBase64SizeFromBase64String(base64String!!)
            Log.e("camera size",size.toString())

        }else if(result.resultCode == RESULT_CANCELED){
            Log.e("camera result status result cancel",result.resultCode.toString())
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)
        var camera : ImageButton = findViewById(R.id.camera)
        camera?.setOnClickListener {
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
        var recyclerView : RecyclerView = findViewById(R.id.mRecyclerView)
        var homeButton : ImageButton  = findViewById(R.id.home)
        homeButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        var adapter = BankRecyclerViewAdapter(this, bankModels)
        setUpBankModels()

        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

    private fun setUpBankModels() {
        var bankNames : Array<String> = resources.getStringArray(R.array.bank_name_txt)
        var bankDescriptions : Array<String> = resources.getStringArray(R.array.bank_description_txt)
        var bankDates : Array<String> = resources.getStringArray(R.array.bank_date_txt)

        for (i in 0 until bankNames.size) {
            val bankModel = BankModel(bankNames[i], bankDescriptions[i], bankDates[i], i)
            bankModels.add(bankModel)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setPopupWindow(view: View?) {

        if (blurred) {
            blurred = false
            Blurry.delete(findViewById(R.id.content))
        } else {
            blurred = true
            val startMs = System.currentTimeMillis()
            Blurry.with(this@BankActivity)
                .radius(25)
                .sampling(2)
                .async()
                .animate(200)
                .onto(findViewById<View>(R.id.content) as ViewGroup)
            Log.d(getString(R.string.app_name),
                "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
        }

//        blurred = !blurred
//        return true

        val popupInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myContentView = popupInflater.inflate(R.layout.popup_window, null)
        myContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = myContentView.measuredHeight

        // popup window set up
        val popupWindow = PopupWindow(this).apply {
            contentView = myContentView
//            width = ViewGroup.LayoutParams.MATCH_PARENT
            width = 1400
            height = 1600
            animationStyle = R.style.PopupAnimation
            isFocusable = true
            isOutsideTouchable = false
            isClippingEnabled = false
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            if (blurred) {
                blurred = false
                Blurry.delete(findViewById(R.id.content))
            }

        })

        // automatically newline tags view
        wrapLayout = myContentView.findViewById(R.id.clip_layout)
        val strs = arrayOf(
            "奔跑吧兄弟",
            "running man",
            "笑傲江湖",
            "快樂大本營",
            "維多利亞的秘密",
            "非誠勿擾",
            "康熙來了",
            "123"
        )
        for (i in 0 until strs.size) {
            val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
            val name = itemLayout.findViewById<View>(R.id.name) as TextView
            name.text = strs[i]
            wrapLayout!!.addView(itemLayout)
        }

        popupWindow.showAtLocation(view, Gravity.CENTER,     0, 0)
        myContentView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()     /* It will dismiss the popup window when tapped in it */
            return@setOnTouchListener true
        }

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
                    val uri1 = FileProvider.getUriForFile(this@BankActivity, "com.example.quizbanktest.fileprovider", f)
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

    private fun uploadImageToImgur(base64String:String){
        if (Constants.isNetworkAvailable(this@BankActivity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(ImgurService::class.java)
            val body = ImgurService.PostBody(base64String)
            val call = api.postBase64(body)

            call.enqueue(object : Callback<String> {
                override fun onResponse(response: Response<String>?, retrofit: Retrofit?) {
                    if (response!!.isSuccess) {
                        val imgurList: String = response.body()
                        Log.i("Response Result", "$imgurList")
                    } else {

                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }
                override fun onFailure(t: Throwable?) {
                    Log.e("Errorrrrr", t?.message.toString())
                }
            })
        } else {
            Toast.makeText(
                this@BankActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
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
                                this@BankActivity,
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
}