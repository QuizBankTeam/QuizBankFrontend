package com.example.quizbanktest.activity.paint

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.BuildCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.utils.*
import com.example.quizbanktest.utils.ConstantsFunction.encodeImage

import com.example.quizbanktest.view.DrawingView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.yalantis.ucrop.UCrop
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException



class PaintActivity : AppCompatActivity() {
    private val SAMPLE_CROPPED_IMG_NAME = "CroppedImage.jpg"
    var sourceUriForUcrop : Uri ?=null //裁減圖片的存放uri
    var saveTempPath:String ?=null //為了color picker設定的temp path 因為他需要圖片的水滴選擇器
    val rotateArray = arrayOf(0, 270, 180, 90) //旋轉角度陣列
    var rotateFlag : Int = 0 //旋轉幾度對應旋轉矩陣的index
    var filterFlag : Boolean = false //是否要濾淨
    var shareFlag : Int = 0 //是否要分享
    var colorTag : String = "#d62828" //預設紅色
    var penFlag: Int = 0 // 0 : brush 1: highlighter
    private lateinit var mProgressDialog: Dialog
    private var mImageButtonCurrentPaint: ImageButton?=null //畫筆

    var idImage = System.currentTimeMillis()/1000 //為了給image的path一個獨特的id ex: QuizBank_32142431.jpg
    val filters = listOf( //目前所有的濾淨
        GPUImageRGBFilter(),
        GPUImageGrayscaleFilter(),
        GPUImageSepiaToneFilter(),
        GPUImageContrastFilter(),
        GPUImageBrightnessFilter(),
        GPUImageExposureFilter(),
        GPUImageDilationFilter(),
        GPUImageCrosshatchFilter(),
        GPUImageMonochromeFilter(),
        GPUImageGaussianBlurFilter(),
        GPUImageSobelThresholdFilter(),
        GPUImageHalftoneFilter(),
        GPUImageLaplacianFilter(),
        GPUImageSubtractBlendFilter(),
        GPUImageLuminanceFilter(),
        GPUImageSaturationBlendFilter(),
        GPUImageAlphaBlendFilter(),
        GPUImageSketchFilter(),
        GPUImageColorInvertFilter(),
        GPUImageSmoothToonFilter()
    )
    val filterDescriptions = mapOf(
        GPUImageRGBFilter::class.java.simpleName to "RGB調節濾鏡",
        GPUImageGrayscaleFilter::class.java.simpleName to "灰階濾鏡",
        GPUImageSepiaToneFilter::class.java.simpleName to "懷舊濾鏡",
        GPUImageContrastFilter::class.java.simpleName to "對比度濾鏡",
        GPUImageBrightnessFilter::class.java.simpleName to "亮度濾鏡",
        GPUImageExposureFilter::class.java.simpleName to "高曝光度濾鏡",
        GPUImageDilationFilter::class.java.simpleName to "膨脹濾鏡",
        GPUImageCrosshatchFilter::class.java.simpleName to "交叉格線濾鏡",
        GPUImageMonochromeFilter::class.java.simpleName to "單色濾鏡",
        GPUImageGaussianBlurFilter::class.java.simpleName to "模糊濾鏡",
        GPUImageSobelThresholdFilter::class.java.simpleName to "邊緣檢測濾鏡",
        GPUImageHalftoneFilter::class.java.simpleName to "半調濾鏡",
        GPUImageLaplacianFilter::class.java.simpleName to "拉普拉斯濾鏡",
        GPUImageSubtractBlendFilter::class.java.simpleName to "混合減除濾鏡",
        GPUImageLuminanceFilter::class.java.simpleName to "亮度濾鏡",
        GPUImageSaturationBlendFilter::class.java.simpleName to "飽和濾鏡",
        GPUImageAlphaBlendFilter::class.java.simpleName to "透明度混合濾鏡",
        GPUImageSketchFilter::class.java.simpleName to "素描濾鏡",
        GPUImageColorInvertFilter::class.java.simpleName to "負片濾鏡",
        GPUImageSmoothToonFilter::class.java.simpleName to "卡通濾鏡"
    )
    private var originBackImageView : Bitmap ?=null //為了使裁減 濾鏡 是在原圖而非繪畫過的圖因此保存原狀態
    private var drawingView: DrawingView?=null //繪畫的事放在上面

    //取的圖片上某點的顏色
    val colorForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val color = intent?.getStringExtra("colorHexCode") //因為顏色是用hex code給的所以要轉成對應的形式
            Log.e("color",color!!)
            drawingView?.setColor("#"+color!!)
            colorTag = "#"+color!!
            val file = File(saveTempPath!!)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Log.e("Delete File", "File deleted successfully!")
                } else {
                    Log.e("Delete File", "Failed to delete file!")
                }
            } else {
                Log.e("Delete File", "File does not exist!")
            }
            Toast.makeText(this, "成功選擇顏色", Toast.LENGTH_SHORT).show()
        }
    }

    //裁減跟旋轉放大縮小的啟動器
    private val uCropActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = UCrop.getOutput(result.data!!)
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val imageBackground: ImageView = findViewById(R.id.iv_background)
            imageBackground.setImageBitmap(bitmap)
            val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
            originBackImageView = getBitmapFromView(flDrawingView)

        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            Log.e("cropResult","error")
            val cropError = UCrop.getError(result.data!!)
            // Handle the cropping error here
        }
    }

    //開啟相簿
    val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() ){
            result->
        Log.e("gallery result status",result.resultCode.toString())
        if(result.resultCode == RESULT_OK&&result.data!=null){
            val contentURI = result.data!!.data
            sourceUriForUcrop  = contentURI
            try {
                val selectedImageBitmap =
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(contentURI!!))
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageBitmap(selectedImageBitmap)
                val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
                originBackImageView = getBitmapFromView(flDrawingView)

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@PaintActivity, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)
        val backButton : ImageButton = findViewById(R.id.image_workspace_back)

        backButton.setOnClickListener{
            if(ConstantsOcrResults.getOcrResult().size!=0){
                Log.e("nav","toolbar")
                val builder = androidx.appcompat.app.AlertDialog.Builder(this,R.style.CustomAlertDialogStyle)
                    .setMessage(" 您確定要離開嗎系統不會保存這次修改喔 ")
                    .setTitle("圖片工作區")
                    .setIcon(R.drawable.baseline_warning_amber_24)
                builder.setPositiveButton("確認") { dialog, which ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                builder.setNegativeButton("取消") { dialog, which ->

                }
                builder.show()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        drawingView = findViewById(R.id.drawing_view)

        Log.e("paint",R.id.ll_paint_colors.toString())

        //下方預設顏色的選擇器偵測
        val linearLayoutPaintColors : LinearLayout = findViewById(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )

        drawingView?.setSizeForBrush(20.toFloat())
        val ib_highlighter : ImageButton = findViewById(R.id.ib_highlighter) //螢光筆
        val ib_brush : ImageButton = findViewById(R.id.ib_brush)//畫筆
        val ib_eraser : ImageButton = findViewById(R.id.ib_eraser)//橡皮擦

        //當畫筆按下時顯示按下的狀態
        ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_press)) //預設為筆

        ib_brush.setOnClickListener{
            drawingView?.cancelEraser()
            showBrushSizeChooserDialog()
            penFlag = 0
            ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_press))
            ib_highlighter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_border_color_24))
            ib_eraser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ib_eraser))
            drawingView?.setColor(colorTag)
        }

        //當橡皮擦按下時顯示按下的狀態
        ib_eraser.setOnClickListener{
            ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_24))
            ib_highlighter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_border_color_24))
            ib_eraser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ib_eraser_press))

            val builder = AlertDialog.Builder(this,R.style.CustomAlertDialogStyle)
            builder.setMessage("使用此橡皮擦並不能復原，是否使用？")
                .setTitle("超級橡皮擦\uD83E\uDE84")
                .setPositiveButton("OK") { dialog, which ->
                    drawingView?.setEraser()
                }
                .setNeutralButton("Cancel") { dialog, which ->
                    penFlag = 0
                    ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_press))
                    ib_highlighter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_border_color_24))
                    ib_eraser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ib_eraser))
                    drawingView?.setColor(colorTag)
                    drawingView?.cancelEraser()
                }
                .show()
        }

        //當營光筆按下時顯示按下的狀態
        ib_highlighter.setOnClickListener{
            drawingView?.cancelEraser()
            penFlag = 1
            ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_24))
            ib_highlighter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_border_color_press))
            ib_eraser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ib_eraser))

            ColorPickerDialog.Builder(this)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("choose",
                    ColorEnvelopeListener { envelope, fromUser ->
                        var color = "#" + envelope.getHexCode()
                        drawingView?.setHighlighterColor(color!!)
                        Toast.makeText(this@PaintActivity,"成功選擇顏色", Toast.LENGTH_SHORT  ).show()
                        mImageButtonCurrentPaint?.setImageDrawable(
                            ContextCompat.getDrawable(this,R.drawable.pallet_normal)
                        )
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    })
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show()
        }

        val ib_rotate : ImageButton = findViewById(R.id.ib_rotateImage)
        ib_rotate.setOnClickListener{
            if(sourceUriForUcrop!=null){
                rotateFlag = rotateFlag+1
                rotateFlag = rotateFlag%4
                val mAngleRotate = (rotateArray[rotateFlag].toString() + "f").toFloat() //圖片旋轉角度
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.rotation = mAngleRotate //設定旋轉角度
            }else{
                Toast.makeText(this,"需要先選擇圖片才能用此功能喔",Toast.LENGTH_SHORT).show()
            }
        }

        val ib_crop : ImageButton = findViewById(R.id.ib_cropImage)
        ib_crop.setOnClickListener{
            if(sourceUriForUcrop!=null){
                // TODO
                val destinationFileName = SAMPLE_CROPPED_IMG_NAME //裁切後的目標名字
                val destinationUri = Uri.fromFile(File(externalCacheDir?.absoluteFile.toString()+File.separator+"QuizBank_"+SAMPLE_CROPPED_IMG_NAME))//裁切後的uri之後會覆蓋掉所以不會重複耗費空間
                val uCrop = UCrop.of(sourceUriForUcrop!!, destinationUri)

                val uCropIntent = uCrop.getIntent(this)
                uCropActivityResultLauncher.launch(uCropIntent)
                sourceUriForUcrop = destinationUri
            }else{
                Toast.makeText(this,"需要先選擇圖片才能用此功能喔",Toast.LENGTH_SHORT).show()
            }
        }

        //返回上一次繪畫的狀態
        val ib_undo : ImageButton = findViewById(R.id.ib_undo)
        ib_undo.setOnClickListener{
            drawingView?.onClickUndo()
        }

        //返回上次的復原狀太
        val ib_redo : ImageButton = findViewById(R.id.ib_redo)
        ib_redo.setOnClickListener{
            drawingView?.onClickRedo()
        }

        val ib_highQuality : ImageButton = findViewById(R.id.ib_highQuality)
        ib_highQuality.setOnClickListener {

            val imageBackground: ImageView = findViewById(R.id.iv_background)
            val backgroundBitmap = getBitmapFromView(imageBackground)
            if(sourceUriForUcrop!=null){
                showProgressDialog("提升畫質中請耐心等候")
                ConstantsRealESRGAN.realEsrgan(
                    sourceUriForUcrop!!, this@PaintActivity,
                    onSuccess = { it1 ->
                        Log.e("it1",it1)
                        val resultBitmap = base64ToBitmap(it1)
                        imageBackground.setImageBitmap(resultBitmap)
                        if (resultBitmap != null) {
                            encodeImage(resultBitmap)?.let { it2 -> Log.e("resultBitmap", it2) }
                        }
                        hideProgressDialog()
                    },
                    onFailure = { it1 ->
                        if(it1 == "429"){
                            showErrorSnackBar("目前伺服器忙碌中請稍後再使用")
                        }
                        showErrorSnackBar("伺服器目前出現了點問題請稍後在試")
                        hideProgressDialog()
                    }
                )
            }else{
                Toast.makeText(this,"要先選擇圖片喔",Toast.LENGTH_SHORT).show()
            }
        }

        //儲存繪畫後的圖片
        val ib_save : ImageButton = findViewById(R.id.ib_save)
        ib_save.setOnClickListener{
            if(true){
                Log.e("in ibsave","success")
                lifecycleScope.launch{
                    val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
                    //Save the image to the device
                    saveBitmapFile(getBitmapFromView(flDrawingView))
                    saveBitmapFileForPicturesDir(getBitmapFromView(flDrawingView))
                }
            }
        }

        //分享按鈕
        val ib_share : ImageButton = findViewById(R.id.ib_share)
        ib_share.setOnClickListener{
            shareFlag = 1
            Log.e("shareFlagIb",shareFlag.toString())
            lifecycleScope.launch{
                val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
                //Save the image to the device
                saveBitmapFile(getBitmapFromView(flDrawingView))
                shareFlag = 0
                Log.e("shareFlagIbFin",shareFlag.toString())
            }
        }

        //從圖片上取得顏色
        val ib_colorPicker : ImageButton = findViewById(R.id.ib_colorPicker)
        ib_colorPicker.setOnClickListener{
            penFlag = 0
            drawingView?.cancelEraser()
            ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_press))
            ib_highlighter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_border_color_24))
            ib_eraser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ib_eraser))
            val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
            val bitmap: Bitmap = getBitmapFromView(flDrawingView)

            val cachePath = File(externalCacheDir, "my_temp_image.jpg")

            val fileOutputStream = FileOutputStream(cachePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()
            val intent = Intent(this, ColorPickerActivity1::class.java)
            saveTempPath = cachePath.absolutePath
            intent.putExtra("bitmap_image", cachePath.absolutePath)

            colorForResult.launch(intent)
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )
        }

        //從圖片選擇器上取得顏色
        val ibPlatte : ImageButton = findViewById(R.id.ib_Palette)
        ibPlatte.setOnClickListener {
            penFlag = 0
            drawingView?.cancelEraser()
            ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_press))
            ib_highlighter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_border_color_24))
            ib_eraser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ib_eraser))
            ColorPickerDialog.Builder(this)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("choose",
                    ColorEnvelopeListener { envelope, fromUser ->
                        var color = "#" + envelope.getHexCode()
                        drawingView?.setColor(color!!)
                        colorTag = color!!
                        Toast.makeText(this@PaintActivity,"成功選擇顏色", Toast.LENGTH_SHORT  ).show()
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    })
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show()
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )
        }
        //套濾鏡
        val ibFilter : ImageButton = findViewById(R.id.ib_filter)
        ibFilter.setOnClickListener {
            if(sourceUriForUcrop!=null){
                if(filterFlag==false){
                    val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
                    originBackImageView = getBitmapFromView(flDrawingView)
                    filterFlag = true
                }
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                // 創建一個選擇濾鏡的 AlertDialog
//                AlertDialog.Builder(this@PaintActivity)
//                    .setTitle("Choose a filter")
//                    .setItems(filters.map { it.javaClass.simpleName }.toTypedArray()) { _, which ->
//                        val gpuImage = GPUImage(this@PaintActivity)
//                        gpuImage.setImage(originBackImageView!!)
//                        gpuImage.setFilter(filters[which])
//                        val bitmapWithFilterApplied: Bitmap = gpuImage.bitmapWithFilterApplied
//                        imageBackground.setImageBitmap(bitmapWithFilterApplied)
//                    }
//                    .show()
                AlertDialog.Builder(this@PaintActivity)
                    .setTitle("選擇一個濾鏡")
                    .setItems(filters.map { filterDescriptions[it.javaClass.simpleName] }.toTypedArray()) { _, which ->
                        val gpuImage = GPUImage(this@PaintActivity)
                        gpuImage.setImage(originBackImageView!!)
                        gpuImage.setFilter(filters[which])
                        val bitmapWithFilterApplied: Bitmap = gpuImage.bitmapWithFilterApplied
                        imageBackground.setImageBitmap(bitmapWithFilterApplied)
                    }
                    .show()
            }else{
                Toast.makeText(this,"需要先選擇圖片才能用此功能喔",Toast.LENGTH_SHORT).show()
            }
        }

        val ibGallery : ImageButton = findViewById(R.id.ib_gallery)
        ibGallery.setOnClickListener{
            choosePhotoFromGallery()
        }

    }

    //顯示畫筆可以選擇的大小
    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size :")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener(View.OnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        })
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener(View.OnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        })

        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener(View.OnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        })
        brushDialog.show()

    }

    fun paintClicked(view:View){
        if(view!== mImageButtonCurrentPaint){
            val imageButton = view as ImageButton
            colorTag = imageButton.tag.toString()
            if(penFlag == 1){
                drawingView?.setHighlighterColor(colorTag)
            }else{
                drawingView?.setColor(colorTag)
            }
            imageButton!!.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }
    }
    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if(bgDrawable!=null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return returnedBitmap
    }

    private suspend fun saveBitmapFile(mBitmap: Bitmap?):String{
        Log.e("in sava","save")
        var result = ""

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
                                this@PaintActivity,
                                "File saved successfully :$result",
                                Toast.LENGTH_SHORT
                            ).show()
                            try {
                                Log.e("shareFlag",shareFlag.toString())
                                if(shareFlag==1){
                                    Log.e("in share",shareFlag.toString())
                                    shareImage(result)
                                    val file = File(result)
                                    file.delete()
                                }
                            }catch (e : java.lang.Exception){

                            }

//                            Log.w("result","$result")
                        } else {
                            Toast.makeText(
                                this@PaintActivity,
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
        if(shareFlag == 1) return ""
        return result
    }

    private suspend fun saveBitmapFileForPicturesDir(mBitmap: Bitmap?): String {
        Log.e("in sava", "save")
        var result = ""

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
                                this@PaintActivity,
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
    fun base64ToBitmap(base64Data: String?): Bitmap? {
        val bytes: ByteArray = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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
    private var backPressedTime: Long = 0
    private val BACK_PRESS_THRESHOLD = 2000  // 2000 milliseconds = 2 seconds
    private fun showRationalDialogForPermissions() {
        androidx.appcompat.app.AlertDialog.Builder(this,R.style.CustomAlertDialogStyle)
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
    override fun onBackPressed() {
        moveTaskToBack(true)
        Log.e("double","pick")
        doubleCheckExit()
    }
    private fun shareImage(result: String){
//        Log.w("share result",result)

        val f = File(externalCacheDir?.absoluteFile.toString()+File.separator+"QuizBank_"+idImage+".jpg")
        val uri1 = FileProvider.getUriForFile(this, "com.example.quizbanktest.fileprovider",f )
        Log.e("uri",uri1.toString())
        try{
            MediaScannerConnection.scanFile(this,arrayOf(result),null){
                    path,uri ->
                Log.e("share path",path.toString())

                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri1)
                shareIntent.type = "image/*"
                startActivity(Intent.createChooser(shareIntent,"Share"))
            }
        }catch(e :Exception){
            Log.e("shareError","share in error")
//            e.printStackTrace()
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
                this@PaintActivity,
                R.color.red
            )
        )
        snackBar.show()
    }
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}