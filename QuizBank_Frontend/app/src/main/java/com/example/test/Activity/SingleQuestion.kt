package com.example.test.Activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.example.test.Adapter.OptionAdapter
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.core.view.setPadding
import com.example.test.R
import com.example.test.databinding.SingleQuestionBinding
import com.example.test.model.Option

class SingleQuestion : AppCompatActivity(){
    private lateinit var questionBinding: SingleQuestionBinding
    private  var optionlist : ArrayList<Option> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questionBinding = SingleQuestionBinding.inflate(layoutInflater)
        setContentView(questionBinding.root)
        init()
        questionBinding.QuestionOption.adapter = OptionAdapter(this, optionlist)
        questionBinding.backBtn.setOnClickListener { finish() }
    }

    private fun init()
    {
        val optionNum = arrayOf("A", "B", "C", "D")
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val image = intent.getIntExtra("Key_image",0)
        val timeLimit = intent.getIntExtra("Key_timeLimit",0).toString() + "秒"
        val options = intent.getStringArrayListExtra("Key_options")
        val tag = intent.getStringArrayListExtra("Key_tag")
        val description = intent.getStringExtra("Key_description")
        if (options != null) {
            if(options.isNotEmpty())
                for(i in options.indices) {
                    val tmpOption = Option(optionNum[i], options[i])
                    optionlist.add(tmpOption)
                }
        }
        questionBinding.QuestionImage.setImageResource(image)
        questionBinding.questionDescription.text = description
        questionBinding.timeLimit.text = timeLimit
        questionBinding.QuestionTitle.text = title
        if(tag != null){
            if(tag.size>0)
            {
                var tagTextView = TextView(this)
                tagTextView.text = tag[0]
                val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                tagTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag24, 0, 0, 0)
                tagTextView.setBackgroundResource(R.drawable.corner_radius_blue)
                val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
                val textSize1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
                var marginHorizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
                tagTextView.setPadding(padding)
                layoutParam.marginStart = marginHorizontal
                layoutParam.marginEnd = marginHorizontal
                tagTextView.layoutParams = layoutParam
                tagTextView.gravity = Gravity.CENTER
                tagTextView.setTextColor(Color.WHITE)
                tagTextView.textSize = textSize1
                questionBinding.QuestionTags.addView(tagTextView)
            }

            if(tag.size>1) {
                var tagTextView1 = TextView(this)
                tagTextView1.text = tag[1]
                val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                tagTextView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag24, 0, 0, 0)
                tagTextView1.setBackgroundResource(R.drawable.corner_radius_blue)
                val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
                val textSize1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
                var marginHorizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
                tagTextView1.setPadding(padding)
                layoutParam.marginStart = marginHorizontal
                layoutParam.marginEnd = marginHorizontal
                tagTextView1.layoutParams = layoutParam
                tagTextView1.gravity = Gravity.CENTER
                tagTextView1.setTextColor(Color.WHITE)
                tagTextView1.textSize = textSize1
                questionBinding.QuestionTags.addView(tagTextView1)
//                questionBinding.QuestionTags.addView(tagTextView)
            }

            if(tag.size>2){
                var tagTextView2 = TextView(this)
                tagTextView2.text = tag[2]
                val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                tagTextView2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag24, 0, 0, 0)
                tagTextView2.setBackgroundResource(R.drawable.corner_radius_blue)
                val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
                val textSize1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
                var marginHorizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
                tagTextView2.setPadding(padding)
                layoutParam.marginStart = marginHorizontal
                layoutParam.marginEnd = marginHorizontal
                tagTextView2.layoutParams = layoutParam
                tagTextView2.gravity = Gravity.CENTER
                tagTextView2.setTextColor(Color.WHITE)
                tagTextView2.textSize = textSize1
                questionBinding.QuestionTags.addView(tagTextView2)
//                questionBinding.QuestionTags.addView(tagTextView)
            }
        }

    }
}
//<TextView
//android:id="@+id/Question_tag0"
//android:layout_width="70dp"
//android:layout_height="wrap_content"
//android:background="@drawable/corner_radius_blue"
//android:padding="5dp"
//android:layout_marginHorizontal="3dp"
//android:gravity="center"
//android:text="2009"
//android:textSize="15dp"
//android:textColor="@color/white"
//android:drawableLeft="@drawable/tag24"/>
//<TextView
//android:id="@+id/Question_tag1"
//android:layout_width="70dp"
//android:layout_height="wrap_content"
//android:background="@drawable/corner_radius_blue"
//android:padding="5dp"
//android:layout_marginHorizontal="3dp"
//android:gravity="center"
//android:text="成大"
//android:textSize="13dp"
//android:textColor="@color/white"
//android:drawableLeft="@drawable/tag24"/>
//<TextView
//android:id="@+id/Question_tag2"
//android:layout_width="70dp"
//android:layout_height="wrap_content"
//android:background="@drawable/corner_radius_blue"
//android:layout_marginHorizontal="3dp"
//android:padding="5dp"
//android:gravity="center"
//android:text="考古題"
//android:textSize="13dp"
//android:textColor="@color/white"
//android:drawableLeft="@drawable/tag24"/>