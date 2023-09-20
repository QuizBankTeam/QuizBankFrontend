package com.example.yiquizapptest

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.yiquizapptest.databinding.ActivityMainBinding
import jp.wasabeef.blurry.Blurry

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing the adapter
        imageViewPagerAdapter = ImageViewPagerAdapter(imageUrlList)

        setUpViewPager()

/*
        findViewById<View>(R.id.button).setOnClickListener {
            val startMs = System.currentTimeMillis()
            Blurry.with(this)
                .radius(25)
                .sampling(1)
                .color(Color.argb(66, 0, 255, 255))
                .async()
                .capture(findViewById(R.id.right_top))
                .into(findViewById(R.id.right_top))

            val bitmap = Blurry.with(this)
                .radius(10)
                .sampling(8)
                .capture(findViewById(R.id.right_bottom)).get()
            findViewById<ImageView>(R.id.right_bottom).setImageDrawable(BitmapDrawable(resources, bitmap))

            Blurry.with(this)
                .radius(25)
                .sampling(4)
                .color(Color.argb(66, 255, 255, 0))
                .capture(findViewById(R.id.left_bottom))
                .getAsync {
                    findViewById<ImageView>(R.id.left_bottom).setImageDrawable(BitmapDrawable(resources, it))
                }

            Log.d(getString(R.string.app_name),
                "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
        }

        findViewById<View>(R.id.button).setOnLongClickListener(object : View.OnLongClickListener {

            private var blurred = false

            override fun onLongClick(v: View): Boolean {
                if (blurred) {
                    Blurry.delete(findViewById(R.id.content))
                } else {
                    val startMs = System.currentTimeMillis()
                    Blurry.with(this@MainActivity)
                        .radius(25)
                        .sampling(2)
                        .async()
                        .animate(500)
                        .onto(findViewById<View>(R.id.content) as ViewGroup)
                    Log.d(getString(R.string.app_name),
                        "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
                }

                blurred = !blurred
                return true
            }
        })
        */
    }
    private fun setUpViewPager() {

        binding.viewPager.adapter = imageViewPagerAdapter

        //set the orientation of the viewpager using ViewPager2.orientation
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        //select any page you want as your starting page
        val currentPageIndex = 1
        binding.viewPager.currentItem = currentPageIndex

        // registering for page change callback
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    //update the image number textview
                    binding.imageNumberTV.text = "${position + 1} / 4"
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        // unregistering the onPageChangedCallback
        binding.viewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
    }
}