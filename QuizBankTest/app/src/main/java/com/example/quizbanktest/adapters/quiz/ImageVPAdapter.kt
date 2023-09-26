package com.example.quizbanktest.adapters.quiz
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.quizbanktest.R
import java.util.*

class ImageVPAdapter (val context: Context, var imageViewList: ArrayList<Bitmap>) : PagerAdapter() {

    fun updateList(newList: ArrayList<Bitmap>){
        imageViewList = newList
        notifyDataSetChanged()
    }
    override fun getCount(): Int {
        return imageViewList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.item_quiz_question_image, container, false)
        val itemImage: ImageView = itemView.findViewById(R.id.question_image)
        itemImage.setImageBitmap(imageViewList[position])
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getItemPosition(`object`: Any): Int {
        if( imageViewList.contains(`object`) ){
            return imageViewList.indexOf(`object`)
        }else{
            return POSITION_NONE
        }
    }
}