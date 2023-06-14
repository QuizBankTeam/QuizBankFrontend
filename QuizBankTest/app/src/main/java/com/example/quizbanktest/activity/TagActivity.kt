package com.example.quizbanktest.activity

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView
import com.example.introducemyself.utils.ConstantsTag
import com.example.quizbanktest.R
import java.lang.Boolean

class TagActivity : AppCompatActivity() {
    private var mTagContainerLayout1: TagContainerLayout? = null
    private var mTagContainerLayout2: TagContainerLayout? = null
    private var mTagContainerLayout3: TagContainerLayout? = null
    private var mTagContainerLayout4: TagContainerLayout? = null
    private var mTagcontainerLayout5: TagContainerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)

        val list1 = ConstantsTag.getList1()
        val list2 = ConstantsTag.getList2()
        val list3 = ConstantsTag.getList3()
        val list4 = ConstantsTag.getList4()
        val list5 = ConstantsTag.getList5()

        mTagContainerLayout1 = findViewById(R.id.tagcontainerLayout1)
        mTagContainerLayout2 = findViewById(R.id.tagcontainerLayout2)
        mTagContainerLayout3 = findViewById(R.id.tagcontainerLayout3)
        mTagContainerLayout4 = findViewById(R.id.tagcontainerLayout4)
        mTagcontainerLayout5 = findViewById(R.id.tagcontainerLayout5)

        mTagContainerLayout1!!.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String) {
                Toast.makeText(
                    this@TagActivity, "click-position:$position, text:$text",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onTagLongClick(position: Int, text: String) {
                val dialog = AlertDialog.Builder(this@TagActivity)
                    .setTitle("long click")
                    .setMessage("You will delete this tag!")
                    .setPositiveButton("Delete") { dialog, which ->
                        if (position < mTagContainerLayout1!!.getChildCount()) {
                            mTagContainerLayout1!!.removeTag(position)
                        }
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.dismiss() }
                    .create()
                dialog.show()
            }

            override fun onSelectedTagDrag(position: Int, text: String) {}
            override fun onTagCrossClick(position: Int) {
//                mTagContainerLayout1.removeTag(position);
                Toast.makeText(
                    this@TagActivity, "Click TagView cross! position = $position",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        mTagContainerLayout3!!.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String) {
                val selectedPositions = mTagContainerLayout3!!.getSelectedTagViewPositions()
                //deselect all tags when click on an unselected tag. Otherwise show toast.
                if (selectedPositions.isEmpty() || selectedPositions.contains(position)) {
                    Toast.makeText(
                        this@TagActivity, "click-position:$position, text:$text",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //deselect all tags
                    for (i in selectedPositions) {
                        mTagContainerLayout3!!.deselectTagView(i)
                    }
                }
            }

            override fun onTagLongClick(position: Int, text: String) {
                mTagContainerLayout3!!.toggleSelectTagView(position)
                val selectedPositions = mTagContainerLayout3!!.getSelectedTagViewPositions()
                Toast.makeText(
                    this@TagActivity, "selected-positions:$selectedPositions",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onSelectedTagDrag(position: Int, text: String) {
                val clip = ClipData.newPlainText("Text", text)
                val view: View = mTagContainerLayout3!!.getTagView(position)
                val shadow = View.DragShadowBuilder(view)
                view.startDrag(clip, shadow, Boolean.TRUE, 0)
            }

            override fun onTagCrossClick(position: Int) {}
        })


        mTagContainerLayout1!!.setTags(list1)

        mTagContainerLayout2!!.setTags(list2)
        mTagContainerLayout3!!.setTags(list3)
        mTagContainerLayout4!!.setTags(list4)

        val colors: MutableList<IntArray> = java.util.ArrayList()
        //int[]color = {backgroundColor, tagBorderColor, tagTextColor, tagSelectedBackgroundColor}
        //int[]color = {backgroundColor, tagBorderColor, tagTextColor, tagSelectedBackgroundColor}
        val col1 = intArrayOf(
            Color.parseColor("#ff0000"),
            Color.parseColor("#000000"),
            Color.parseColor("#ffffff"),
            Color.parseColor("#999999")
        )
        val col2 = intArrayOf(
            Color.parseColor("#0000ff"),
            Color.parseColor("#000000"),
            Color.parseColor("#ffffff"),
            Color.parseColor("#999999")
        )

        colors.add(col1)
        colors.add(col2)

        mTagcontainerLayout5!!.setTags(list5, colors)
        val text = findViewById<View>(R.id.text_tag) as EditText
        val btnAddTag = findViewById<View>(R.id.btn_add_tag) as Button
        btnAddTag.setOnClickListener {
            mTagContainerLayout1!!.addTag(text.text.toString())
            // Add tag in the specified position
            //                mTagContainerLayout1.addTag(text.getText().toString(), 4);
        }

    }


    class TagRecyclerViewAdapter(private val mContext: Context, private val mData: Array<String>) :
        RecyclerView.Adapter<TagRecyclerViewAdapter.TagViewHolder>() {
        private var mOnClickListener: View.OnClickListener? = null
        override fun getItemCount(): Int {
            return 10
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
            return TagViewHolder(
                LayoutInflater.from(mContext)
                    .inflate(R.layout.item_tag, parent, false), mOnClickListener
            )
        }

        override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
            holder.tagContainerLayout.setTags(*mData)
            holder.button.setOnClickListener(mOnClickListener)
        }

        fun setOnClickListener(listener: View.OnClickListener?) {
            mOnClickListener = listener
        }

        inner class TagViewHolder(v: View, var clickListener: View.OnClickListener?) :
            RecyclerView.ViewHolder(v), View.OnClickListener {
            var tagContainerLayout: TagContainerLayout
            var button: Button

            init {
                tagContainerLayout =
                    v.findViewById<View>(R.id.tagcontainerLayout) as TagContainerLayout
                button = v.findViewById<View>(R.id.button) as Button

            }

            override fun onClick(v: View) {
                if (clickListener != null) {
                    clickListener!!.onClick(v)
                }
            }
        }
    }

}