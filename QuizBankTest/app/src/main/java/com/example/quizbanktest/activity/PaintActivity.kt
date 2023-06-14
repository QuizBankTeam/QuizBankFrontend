package com.example.quizbanktest.activity

import android.Manifest
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.example.quizbanktest.R

import com.example.quizbanktest.draw.DrawingView
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
    var sourceUriForUcrop : Uri ?=null
    var saveTempPath:String ?=null
    val rotateArray = arrayOf(0, 270, 180, 90)
    var rotateFlag : Int = 0
    var filterFlag : Boolean = false
    var shareFlag : Int = 0
    var colorTag : String = "#d62828" //預設紅色
    var penFlag: Int = 0 // 0 : brush 1: highlighter
    private var mImageButtonCurrentPaint: ImageButton?=null

    var idImage = System.currentTimeMillis()/1000
    val filters = listOf(
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
    private var originBackImageView : Bitmap ?=null
    private var drawingView: DrawingView?=null

    val colorForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val color = intent?.getStringExtra("colorHexCode")
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

        var toolBar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_paint_detail)
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            Log.e("in action bar","not null")
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            Log.e("nav","toolbar")
        }

        toolBar.setNavigationOnClickListener{
            Log.e("nav","toolbar")
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        drawingView = findViewById(R.id.drawing_view)

        Log.e("paint",R.id.ll_paint_colors.toString())

        val linearLayoutPaintColors : LinearLayout = findViewById(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )

        drawingView?.setSizeForBrush(20.toFloat())
        val ib_highlighter : ImageButton = findViewById(R.id.ib_highlighter)
        val ib_brush : ImageButton = findViewById(R.id.ib_brush)
        val ib_eraser : ImageButton = findViewById(R.id.ib_eraser)
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

        ib_eraser.setOnClickListener{
            ib_brush.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_brush_24))
            ib_highlighter.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_border_color_24))
            ib_eraser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ib_eraser_press))

            val builder = AlertDialog.Builder(this)
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
                val mAngleRotate = (rotateArray[rotateFlag].toString() + "f").toFloat()
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.rotation = mAngleRotate
            }
        }

        val ib_crop : ImageButton = findViewById(R.id.ib_cropImage)
        ib_crop.setOnClickListener{
            if(sourceUriForUcrop!=null){
                // TODO
                val destinationFileName = SAMPLE_CROPPED_IMG_NAME
                val destinationUri = Uri.fromFile(File(externalCacheDir?.absoluteFile.toString()+File.separator+"QuizBank_"+SAMPLE_CROPPED_IMG_NAME))
                val uCrop = UCrop.of(sourceUriForUcrop!!, destinationUri)

                val uCropIntent = uCrop.getIntent(this)
                uCropActivityResultLauncher.launch(uCropIntent)
                sourceUriForUcrop = destinationUri
            }
        }

        val ib_undo : ImageButton = findViewById(R.id.ib_undo)
        ib_undo.setOnClickListener{
            drawingView?.onClickUndo()
        }

        val ib_redo : ImageButton = findViewById(R.id.ib_redo)
        ib_redo.setOnClickListener{
            drawingView?.onClickRedo()
        }

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
                AlertDialog.Builder(this@PaintActivity)
                    .setTitle("Choose a filter")
                    .setItems(filters.map { it.javaClass.simpleName }.toTypedArray()) { _, which ->
                        val gpuImage = GPUImage(this@PaintActivity)
                        gpuImage.setImage(originBackImageView!!)
                        gpuImage.setFilter(filters[which])
                        val bitmapWithFilterApplied: Bitmap = gpuImage.bitmapWithFilterApplied
                        imageBackground.setImageBitmap(bitmapWithFilterApplied)
                    }
                    .show()
            }
        }

        val ibGallery : ImageButton = findViewById(R.id.ib_gallery)
        ibGallery.setOnClickListener{
            choosePhotoFromGallery()
        }

        val ibQuality : ImageButton = findViewById(R.id.ib_highQuality)
        ibQuality.setOnClickListener{
            //TODO
        }
    }

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

    private fun showRationalDialogForPermissions() {
        androidx.appcompat.app.AlertDialog.Builder(this)
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
}