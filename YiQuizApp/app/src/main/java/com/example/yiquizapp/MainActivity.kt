package com.example.yiquizapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    lateinit var searchView: SearchView
    lateinit var menuButton: ImageButton
    private var wrapLayout: WrapLayout? = null

    private val mBackgroundBlurRadius = 80
    private val mBlurBehindRadius = 20

    // We set a different dim amount depending on whether window blur is enabled or disabled
    private val mDimAmountWithBlur = 0.1f
    private val mDimAmountNoBlur = 0.4f

    // We set a different alpha depending on whether window blur is enabled or disabled
    private val mWindowBackgroundAlphaWithBlur = 170
    private val mWindowBackgroundAlphaNoBlur = 255

    // Use a rectangular shape drawable for the window background. The outline of this drawable
    // dictates the shape and rounded corners for the window background blur area.
    private var mWindowBackgroundDrawable: Drawable? = null

    var bankModels = ArrayList<BankModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var recyclerView : RecyclerView = findViewById(R.id.mRecyclerView)
        var adapter = BankRecyclerViewAdapter(this, bankModels)
        setUpBankModels()

        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        mWindowBackgroundDrawable = getDrawable(R.drawable.window_background)
//        getWindow().setBackgroundDrawable(mWindowBackgroundDrawable)
//        if (buildIsAtLeastS()) {
//            // Enable blur behind. This can also be done in xml with R.attr#windowBlurBehindEnabled
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//
//            // Register a listener to adjust window UI whenever window blurs are enabled/disabled
//            setupWindowBlurListener();
//        } else {
//            // Window blurs are not available prior to Android S
//            updateWindowForBlurs(false /* blursEnabled */);
//        }
//
//        // Enable dim. This can also be done in xml, see R.attr#backgroundDimEnabled
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
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

    fun setPopupWindow(view: View?) {

        val popupInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myContentView = popupInflater.inflate(R.layout.popup_window, null)
        myContentView.measure(View.MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val popupHeight = myContentView.measuredHeight

        // popup window set up
        val popupWindow = PopupWindow(this).apply {
            contentView = myContentView
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            animationStyle = R.style.PopupAnimation
            isFocusable = true
            isOutsideTouchable = true
            isClippingEnabled = false
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        // automatically newline tags view
        wrapLayout = myContentView.findViewById(R.id.hot_search_layout)
        val strs = arrayOf(
            "奔跑吧兄弟",
            "running man",
            "笑傲江湖",
            "快樂大本營",
            "維多利亞的秘密",
            "非誠勿擾",
            "康熙來了"
        )
        for (i in 0 until strs.size) {
            val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
            val name = itemLayout.findViewById<View>(R.id.name) as TextView
            name.text = strs[i]
            wrapLayout!!.addView(itemLayout)
        }

        popupWindow.showAtLocation(view, Gravity.CENTER,     0, 0)
        myContentView.setOnTouchListener { v, event ->
            popupWindow.dismiss()     // It will dismiss the popup window when tapped in it
            true
        }

        val imageview = findViewById<ImageView>(R.id.imageview)
        val bitmap = getBitmapFromView(imageview)
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun getBitmapFromView(view: View, defaultColor: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(defaultColor)
        view.draw(canvas)
        return bitmap
    }

}