package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.lang.reflect.TypeVariable

class DrawingView(context: Context, attrs:AttributeSet) : View(context,attrs) {

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap:Bitmap?=null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null

    private var mBrushSize:Float = 0.toFloat()
    private var color = Color.WHITE
    private var canvas: Canvas?=null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init{
        setUpDrawing()
    }
    fun onClickUndo(){//按下返回鍵使其復原到前一個狀態
        if(mPaths.size>0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()//更新會呼叫 ondraw
        }
    }
    private fun setUpDrawing(){//設置一開始的初始化
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color=color
        mDrawPaint!!.style = Paint.Style.STROKE//描邊
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND//線段的和線段的連接方式類似轉彎處是要甚麼形狀
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND//線段的開始和結尾的那個下筆和離筆是要甚麼形狀
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
       // mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { //當設備的螢幕大小換會自動將畫布調整成適合的大小
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)//8888分別是透明度和RGB然後用8位元256 bits去紀錄
        canvas = Canvas(mCanvasBitmap!!)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint) //代表從原圖的方向0f 0f為整張圖的左上角開始並且大小是整張圖

        for(path in mPaths){//為了使你會畫紀錄不會因為離手而不見會保存在畫面上
            mDrawPaint!!.strokeWidth = path.brushThickness //設置筆刷寬度
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty) { //加這個是為了可以看到你畫畫的筆跡就是如果不加的話你要離手了他才會將你剛剛畫的顯示在螢幕
            mDrawPaint!!.color = mDrawPath!!.color
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness

            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    fun setColor(newColor : String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN->{//按下的瞬間 紀錄按下的位置並將初始位置移到那個地方
                mDrawPath!!.color =color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)//move to不會畫在上面只會移動位置
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)//會直接畫上去從你move to的地方
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)//記錄你畫的
                mDrawPath = CustomPath(color,mBrushSize)
            }

            else ->return false
        }
        invalidate()//更新會呼叫 ondraw

        return true
    }

    fun setSizeForBrush(newSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)

        mDrawPaint!!.strokeWidth = mBrushSize
    }

    internal inner class CustomPath(var color:Int ,var brushThickness:Float) : Path(){

    }


}