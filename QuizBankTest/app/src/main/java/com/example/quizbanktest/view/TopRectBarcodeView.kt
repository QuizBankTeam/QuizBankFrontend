package com.example.quizbanktest.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.AttributeSet
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.Size
import kotlin.math.roundToInt

class TopRectBarcodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BarcodeView(context, attrs, defStyleAttr) {

    override fun getFramingRectSize(): Size {
        return Size(Resources.getSystem().displayMetrics.widthPixels, dpToPx(212))
    }
    private fun dpToPx(dp: Int): Int {
        return ((dp * Resources.getSystem().displayMetrics.density).roundToInt())
    }
    override fun calculateFramingRect(
        container: Rect?,
        surface: Rect?
    ): Rect {
        // create new rect instance that hold the container.
        val intersection = Rect(container)
        // specify the position of left direction.
        intersection.left = dpToPx(15)
        // specify the position of top direction.
        intersection.top = dpToPx(198)
        // specify the position of right direction.
        intersection.right =
            framingRectSize.width - dpToPx(15)
        // specify the position of bottom direction.
        intersection.bottom =
            framingRectSize.height + dpToPx(190)
        return intersection
    }
}