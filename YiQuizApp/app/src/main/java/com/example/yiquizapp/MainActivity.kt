package com.example.yiquizapp

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
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

        val imageView = findViewById<View>(R.id.imageview) as ImageView
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.white_book
        )
        imageView.viewTreeObserver.addOnPreDrawListener {
            blur(bitmap, imageView)
            true
        }

    }

    object FastBlur {
        fun doBlur(sentBitmap: Bitmap?, radius: Int, canReuseInBitmap: Boolean): Bitmap? {

            val bitmap: Bitmap?
            bitmap = if (canReuseInBitmap) {
                sentBitmap
            } else {
                //决定图片像素点的存储，即用图片查看器看图片属性时候的位深参数。
                sentBitmap!!.copy(sentBitmap.config, true)
            }
            if (radius < 1) {
                return null
            }
            val w = bitmap!!.width
            val h = bitmap.height
            val pix = IntArray(w * h)
            bitmap.getPixels(pix, 0, w, 0, 0, w, h)
            val wm = w - 1
            val hm = h - 1
            val wh = w * h
            val div = radius + radius + 1
            val r = IntArray(wh)
            val g = IntArray(wh)
            val b = IntArray(wh)
            var rsum: Int
            var gsum: Int
            var bsum: Int
            var x: Int
            var y: Int
            var i: Int
            var p: Int
            var yp: Int
            var yi: Int
            var yw: Int
            val vmin = IntArray(Math.max(w, h))
            var divsum = div + 1 shr 1
            divsum *= divsum
            val dv = IntArray(256 * divsum)
            i = 0
            while (i < 256 * divsum) {
                dv[i] = i / divsum
                i++
            }
            yi = 0
            yw = yi
            val stack = Array(div) {
                IntArray(
                    3
                )
            }
            var stackpointer: Int
            var stackstart: Int
            var sir: IntArray
            var rbs: Int
            val r1 = radius + 1
            var routsum: Int
            var goutsum: Int
            var boutsum: Int
            var rinsum: Int
            var ginsum: Int
            var binsum: Int
            y = 0
            while (y < h) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                i = -radius
                while (i <= radius) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))]
                    sir = stack[i + radius]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rbs = r1 - Math.abs(i)
                    rsum += sir[0] * rbs
                    gsum += sir[1] * rbs
                    bsum += sir[2] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    i++
                }
                stackpointer = radius
                x = 0
                while (x < w) {
                    r[yi] = dv[rsum]
                    g[yi] = dv[gsum]
                    b[yi] = dv[bsum]
                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum
                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]
                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]
                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm)
                    }
                    p = pix[yw + vmin[x]]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum
                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer % div]
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]
                    yi++
                    x++
                }
                yw += w
                y++
            }
            x = 0
            while (x < w) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                yp = -radius * w
                i = -radius
                while (i <= radius) {
                    yi = Math.max(0, yp) + x
                    sir = stack[i + radius]
                    sir[0] = r[yi]
                    sir[1] = g[yi]
                    sir[2] = b[yi]
                    rbs = r1 - Math.abs(i)
                    rsum += r[yi] * rbs
                    gsum += g[yi] * rbs
                    bsum += b[yi] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    if (i < hm) {
                        yp += w
                    }
                    i++
                }
                yi = x
                stackpointer = radius
                y = 0
                while (y < h) {

// Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] =
                        -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum
                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]
                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]
                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w
                    }
                    p = x + vmin[y]
                    sir[0] = r[p]
                    sir[1] = g[p]
                    sir[2] = b[p]
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum
                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer]
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]
                    yi += w
                    y++
                }
                x++
            }
            bitmap.setPixels(pix, 0, w, 0, 0, w, h)
            return bitmap
        }
    }


    private fun blur(bkg: Bitmap, view: View) {
        val startMs = System.currentTimeMillis()
        val scaleFactor = 8f
        val radius = 20f
        var overlay = Bitmap.createBitmap(
            (view.measuredWidth / scaleFactor).toInt(),
            (view.measuredHeight / scaleFactor).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(overlay!!)
        canvas.translate(
            -view.left / scaleFactor, -view.top
                    / scaleFactor
        )
        canvas.scale(1 / scaleFactor, 1 / scaleFactor)
        val paint = Paint()
        paint.setFlags(Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bkg, 0f, 0f, paint)
        overlay = FastBlur.doBlur(overlay, radius.toInt(), true)
        view.background = BitmapDrawable(resources, overlay)
    }
}
