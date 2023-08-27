package com.example.quizbanktest.fragment

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
        Log.d("in single record page", "")
        init()



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

    private fun initWithoutNetwork() {
        val tmpMembers = arrayListOf<String>("jacky")
        val tmpQuestionId = arrayOf("question id 1", "question id 2","question id 3","question id 4","question id 5","question id 6")
        val tmpQuestionRecordId = arrayListOf<String>("QuestionRecord id 1", "QuestionRecord id 2","QuestionRecord id 3","QuestionRecord id 4")
        val tmpQuizRecord = QuizRecord("Record Id 1", "test quiz", "Quiz id 1", "single", 80, 120, "2023-07-20 23:24:30", "2023-07-20 23:34:30", tmpMembers, tmpQuestionRecordId)
        recordList.add(tmpQuizRecord)
    }
}