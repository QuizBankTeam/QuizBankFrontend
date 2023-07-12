package com.example.quizbanktest.activity
import android.app.AlertDialog
import android.content.ClipData

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView
import com.example.introducemyself.utils.ConstantsTag
import com.example.quizbanktest.R
import java.lang.Boolean

class TagActivity : AppCompatActivity() {
    private var mTagContainerLayout1: TagContainerLayout? = null
    private var mTagContainerLayout2: TagContainerLayout? = null
    private var mTagContainerLayout3: TagContainerLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)

        val list1 = ConstantsTag.getList1()
        val list2 = ConstantsTag.getList2()
        val list3 = ConstantsTag.getList3()


        mTagContainerLayout1 = findViewById(R.id.tagcontainerLayout1)
        mTagContainerLayout2 = findViewById(R.id.tagcontainerLayout2)
        mTagContainerLayout3 = findViewById(R.id.tagcontainerLayout3)


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


        val text = findViewById<View>(R.id.text_tag) as EditText
        val btnAddTag = findViewById<View>(R.id.btn_add_tag) as TextView
        btnAddTag.setOnClickListener {
            mTagContainerLayout1!!.addTag(text.text.toString())
            // Add tag in the specified position
            //                mTagContainerLayout1.addTag(text.getText().toString(), 4);
        }

        var enterButton : TextView = findViewById(R.id.enter_tag)
        var cancelButton : TextView = findViewById(R.id.cancel_tag)

        enterButton.setOnClickListener {
            Toast.makeText(this@TagActivity,"this is a enter button",Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            Toast.makeText(this@TagActivity,"this is a cancel button",Toast.LENGTH_SHORT).show()
        }
    }





}

