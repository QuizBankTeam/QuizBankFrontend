package com.example.quizbanktest.activity.scan

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.group.GroupListActivity
import com.example.quizbanktest.utils.ConstantsFunction
import com.example.quizbanktest.utils.ConstantsScanServiceFunction
import com.example.quizbanktest.view.CustomViewfinderView
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.SourceData
import com.journeyapps.barcodescanner.camera.PreviewCallback
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.Arrays

class ScanActivity :AppCompatActivity(){
    private lateinit var barcodeView: DecoratedBarcodeView
    private var beepManager: BeepManager? = null
    private var lastText: String? = null
    private var bmp : Bitmap ?=null
    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                return
            }
            lastText = result.text
            barcodeView!!.setStatusText(result.text)
            beepManager!!.playBeepSoundAndVibrate()

            //Added preview of scanned barcode
            val imageView = findViewById<ImageView>(R.id.barcodePreview)
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW))
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        barcodeView = findViewById(R.id.barcode_scanner1)
        val formats: Collection<BarcodeFormat> =
            Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView.getBarcodeView().decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(callback)

        barcodeView.barcodeView.cameraSettings.isContinuousFocusEnabled = true
        beepManager = BeepManager(this)
        val customViewFinder = findViewById<CustomViewfinderView>(R.id.zxing_viewfinder_view)
        customViewFinder.setLaserStart()
        val save_btn = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.save)


        save_btn.setOnClickListener {
            customViewFinder.setLaserStop()
            barcodeView.barcodeView.cameraInstance.requestPreview(object : PreviewCallback {
                override fun onPreview(sourceData: SourceData) {
                    sourceData.cropRect = Rect(barcodeView.barcodeView.previewFramingRect)
                    bmp = sourceData.bitmap
                    runOnUiThread {
                        pause(null)
                    }
                }
                override fun onPreviewError(e: Exception?) {
                    TODO("Not yet implemented")
                }
            })
        }
        doubleCheckExit()
    }
    override fun onResume() {
        super.onResume()
        barcodeView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView!!.pause()
    }

    fun pause(view: View?) {
        barcodeView!!.pause()
        if(bmp!=null){
            processScan(bmp!!)
        }
    }

    fun resume(view: View?) {
        barcodeView!!.resume()
    }

    fun triggerScan(view: View?) {
        barcodeView!!.decodeSingle(callback)
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
        val resultIntent = Intent()
        setResult(Activity.RESULT_CANCELED, resultIntent)
        Log.e("on scan back press","true")
        finish()
    }
    override fun onBackPressed() {
        val resultIntent = Intent()
        setResult(Activity.RESULT_CANCELED, resultIntent)
        Log.e("on scan back press","true")
        finish()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
    fun processScan(bmp:Bitmap){
        val progressDialog = ProgressDialog(this@ScanActivity)
        progressDialog.setMessage("處理中")
        progressDialog.setCancelable(false)
        progressDialog.show()
        ConstantsScanServiceFunction.scanPhotoBase64ToOcrText(ConstantsFunction.encodeImage(bmp)!!, this@ScanActivity,1, onSuccess = { it1 ->
            ConstantsOcrResults.setOcrResult(it1)
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            progressDialog.dismiss()
            finish()
        }, onFailure = { it1 ->
            progressDialog.dismiss()
            Toast.makeText(this@ScanActivity,"請對準後再測試一次",Toast.LENGTH_SHORT).show()
            onResume()
        })
    }
}
