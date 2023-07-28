package com.example.quizbanktest.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizbanktest.R
import com.example.quizbanktest.databinding.ListMpRecordBinding
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.models.QuizRecord

class MultiRecordPage: Fragment() {
    private lateinit var recordBinding: ListMpRecordBinding
    private var QuizList : ArrayList<Quiz> = ArrayList()
    private var recordList: ArrayList<QuizRecord> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recordBinding = ListMpRecordBinding.inflate(inflater, container, false)
        return recordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
//        recordBinding.QuizRecordList.layoutManager = LinearLayoutManager(requireContext())
//        recordBinding.QuizRecordList.setHasFixedSize(true)
//        val recordAdapter = SPRecordAdapter(requireActivity(), recordList)
//        recordBinding.QuizRecordList.adapter = recordAdapter
//        recordBinding.QuizRecordList.isClickable = true


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
    private fun init(){

    }
    companion object {
    }
}