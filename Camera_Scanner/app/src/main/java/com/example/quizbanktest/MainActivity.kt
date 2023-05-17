package com.example.quizbanktest
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.adapters.RecentViewAdapter
import com.example.quizbanktest.adapters.RecommendViewAdapter
import com.example.quizbanktest.adapters.WrongViewAdapter
import com.example.quizbanktest.databinding.ActivityMainBinding
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.network.ImgurService
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuestionBank
import com.example.quizbanktest.utils.ConstantsRecommend
import com.example.quizbanktest.utils.ConstantsWrong
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding?=null
    private val SAMPLE_CROPPED_IMG_NAME = "CroppedImage.jpg"
    private val uCropActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = UCrop.getOutput(result.data!!)
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val base64String = bitmapToBase64(bitmap)
            uploadImageToImgur(base64String)
            Log.e("cropResult",uri.toString())
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
                    MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                val saveImageToInternalStorage =
                    saveImageToInternalStorage(selectedImageBitmap)
                Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")


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

        if(result.resultCode == RESULT_OK&&result.data!=null){
            val thumbnail: Bitmap = result.data!!.extras!!.get("data") as Bitmap // Bitmap from camera

            val saveImageToInternalStorage =
                saveImageToInternalStorage(thumbnail)
            val f =
                File(externalCacheDir?.absoluteFile.toString() + File.separator + "QuizBank_" + idImage + ".jpg")
            val uri1 = FileProvider.getUriForFile(this, "com.example.quizbanktest.fileprovider", f)

            val sourceUri = uri1  // The Uri of the image you want to crop
            val destinationFileName = SAMPLE_CROPPED_IMG_NAME
            val destinationUri = Uri.fromFile(File(externalCacheDir?.absoluteFile.toString()+File.separator+"QuizBank_"+SAMPLE_CROPPED_IMG_NAME))

            val uCrop = UCrop.of(sourceUri, destinationUri)
            uCrop.withAspectRatio(1f, 1f)
            uCrop.withMaxResultSize(800, 800)

            val uCropIntent = uCrop.getIntent(this)
            uCropActivityResultLauncher.launch(uCropIntent)

            Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

//            binding?.cameraTest!!.setImageBitmap(thumbnail)



        }else if(result.resultCode == RESULT_CANCELED){
            Log.e("camera result status result cancel",result.resultCode.toString())
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupRecentRecyclerView(ConstantsQuestionBank.getQuestions())
        setupRecommendRecyclerView(ConstantsRecommend.getQuestions())
        setupWrongListRecyclerView(ConstantsWrong.getQuestions())
        binding?.camera?.setOnClickListener {
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

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
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
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    var idImage = System.currentTimeMillis()/1000

    private suspend fun saveBitmapFile(mBitmap: Bitmap?):String{
        var result = ""
        if(mBitmap!=null){
            var base64URL = encodeImage(mBitmap)
            uploadImageToImgur(base64URL!!)
        }
        withContext(Dispatchers.IO){
            if(mBitmap != null){
                try{
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.JPEG,90,bytes)

                    val f = File(externalCacheDir?.absoluteFile.toString()+File.separator+"QuizBank_"+idImage+".jpg")
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    result = f.absolutePath
//                    Log.e("result",result)
                    runOnUiThread {
                        if (!result.isEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "File saved successfully :$result",
                                Toast.LENGTH_SHORT
                            ).show()
//                            shareImage(result)
//                            Log.w("result","$result")
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

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        var file : String ?=null
        lifecycleScope.launch {
            file =  saveBitmapFile(bitmap)
        }
        var base64Url = bitmapToBase64(bitmap)


        val estimatedBase64Size = estimateBase64SizeFromBase64String(base64Url)

        Log.e("bytes",estimatedBase64Size.toString())
        if(file!=null){
            return file as String
        }
        return "null"


    }
    private fun uploadImageToImgur(base64String:String){
        if (Constants.isNetworkAvailable(this@MainActivity)) {

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
                this@MainActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupRecentRecyclerView(quizBankList: ArrayList<QuestionBankModel>) {

        binding?.recentQuizBankList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding?.recentQuizBankList?.setHasFixedSize(true)

        val placesAdapter = RecentViewAdapter(this, quizBankList)
        binding?.recentQuizBankList?.adapter = placesAdapter


    }

    private fun setupWrongListRecyclerView(wrongList: ArrayList<QuestionModel>) {

        binding?.recentWrongList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding?.recentWrongList?.setHasFixedSize(true)

        val placesAdapter = WrongViewAdapter(this, wrongList)
        binding?.recentWrongList?.adapter = placesAdapter
    }

    private fun setupRecommendRecyclerView(recommendList: ArrayList<QuestionModel>) {

        binding?.recentRecommendList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding?.recentRecommendList?.setHasFixedSize(true)

        val placesAdapter = RecommendViewAdapter(this, recommendList)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2

        private const val IMAGE_DIRECTORY = "QuizTest"

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}