package com.example.quizbanktest.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class WrapLayout : ViewGroup {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val maxHeight = getWrapHeight(widthMeasureSpec)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxHeight)
    }

    private fun getWrapHeight(widthMeasureSpec: Int): Int {
        var params: WrapLayoutParams
        var row = 1
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec) //layout擁有最大的width
        var occupancyWidth = 0 //佔用的width
        var occupancyHeight = 0 //佔用的height
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            params = child.layoutParams as WrapLayoutParams
            //            除了本身的width,還需加上左右兩邊的margin
            val childWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            //             除了本身的height,還需加上上下兩邊的margin
            val childHeight = child.measuredHeight + params.topMargin + params.bottomMargin
            occupancyWidth += childWidth
            //            if 超過最大的寬度,將child分配到下一行
            if (occupancyWidth > maxWidth) {
                row++
                occupancyWidth = childWidth
            }
            occupancyHeight = childHeight * row
        }
        return occupancyHeight
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var params: WrapLayoutParams
        var row = 0 //因為是座標,排滿一排才增加一行,因此預設為0
        var occupancyWidthPostion = left //目前佔用的左座標
        var occupancyHeightPostion = top // 目前佔用的top座標
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            params = child.layoutParams as WrapLayoutParams //為了取得邊界
            val childWidth = child.measuredWidth + params.leftMargin + params.rightMargin
            val childHeight = child.measuredHeight + params.topMargin + params.bottomMargin
            if (occupancyWidthPostion + childWidth > right) {
                row++
                occupancyWidthPostion = left
            }
            occupancyHeightPostion = row * childHeight
            /**
             * layout(left,top,right,bottom)
             * left = 左邊座標＝目前佔用的左座標+leftmargin
             * top(距離parent top的座標) = 目前佔用的top座標(隨著row增加會增加)
             * right ＝左邊座標＋child's width
             * bottom = 目前佔用的top座標＋child's height
             */
            child.layout(
                occupancyWidthPostion + params.leftMargin, occupancyHeightPostion,
                occupancyWidthPostion + childWidth, occupancyHeightPostion + childHeight
            )
            occupancyWidthPostion += childWidth
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return WrapLayoutParams(context, attrs)
    }

    inner class WrapLayoutParams(c: Context?, attrs: AttributeSet?) : MarginLayoutParams(c, attrs)
}