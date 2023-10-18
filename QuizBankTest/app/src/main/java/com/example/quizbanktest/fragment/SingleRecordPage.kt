package com.example.quizbanktest.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.SPRecordAdapter
import com.example.quizbanktest.databinding.ListSpRecordBinding
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz
import com.example.quizbanktest.utils.ConstantsQuizRecord
import java.lang.ref.WeakReference

class SingleRecordPage: Fragment() {
    companion object {
        var recordListImages =  ArrayList< ArrayList< ArrayList<WeakReference<String>> > >()
    }
    private lateinit var recordBinding: ListSpRecordBinding
    private lateinit var recordAdapter: SPRecordAdapter
    private var recordList = ArrayList<QuizRecord>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recordBinding = ListSpRecordBinding.inflate(inflater, container, false)
        return recordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init(){
        val quizRecordType = Constants.quizTypeSingle
        ConstantsQuizRecord.getAllQuizRecords(requireContext(), quizRecordType, onSuccess = { returnRecordList->
            recordList = returnRecordList
            recordBinding.QuizRecordList.layoutManager = LinearLayoutManager(requireContext())
            recordBinding.QuizRecordList.setHasFixedSize(true)
            recordAdapter = SPRecordAdapter(requireActivity(), recordList)
            recordBinding.QuizRecordList.adapter = recordAdapter
            recordBinding.QuizRecordList.isClickable = true
        }, onFailure = {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            initWithoutNetwork()
        })
    }
    fun onDeleteBtnClick(deleteBtnVisible: Boolean){
        if(deleteBtnVisible){
            recordAdapter.setDeleteBtnVisible(true)
            recordAdapter.notifyDataSetChanged()
            recordAdapter.setDeleteClickListener(object : SPRecordAdapter.SelectOnDeleteClickListener{
                override fun onclick(position: Int, holder: SPRecordAdapter.MyViewHolder) {
                    val deleteBuilder = AlertDialog.Builder(requireContext())
                    deleteBuilder.setTitle("確定刪除考試紀錄?")
                    deleteBuilder.setPositiveButton("確定") { dialog, which ->
                        ConstantsQuizRecord.deleteQuizRecord(requireContext(), recordList[position]._id, onSuccess = {
                            recordList.removeAt(position)
                            recordAdapter.notifyDataSetChanged()
                        }, onFailure = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        })
                    }
                    deleteBuilder.setNegativeButton("取消"){ dialog, which ->
                    }
                    deleteBuilder.show()
                }
            })
        }else{
            recordAdapter.setDeleteBtnVisible(false)
            recordAdapter.notifyDataSetChanged()
        }
    }

    private fun initWithoutNetwork() {
        val tmpMembers = arrayListOf<String>("jacky")
        val tmpQuestionId = arrayOf("question id 1", "question id 2","question id 3","question id 4","question id 5","question id 6")
        val tmpQuestionRecordId = arrayListOf<String>("QuestionRecord id 1", "QuestionRecord id 2","QuestionRecord id 3","QuestionRecord id 4")
        val tmpQuizRecord = QuizRecord("Record Id 1", "test quiz", "Quiz id 1", "asdf", "single", 80, 120, "2023-07-20 23:24:30", "2023-07-20 23:34:30", tmpMembers, tmpQuestionRecordId)
        recordList.add(tmpQuizRecord)
    }
}