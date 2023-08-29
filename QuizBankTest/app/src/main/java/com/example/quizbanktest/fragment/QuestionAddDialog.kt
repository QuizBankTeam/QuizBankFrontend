package com.example.quizbanktest.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizbanktest.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QuestionAddDialog : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (context == null) return super.onCreateDialog(savedInstanceState)
        val bottomSheetDialog = BottomSheetDialog(context!!,  R.style.CustomAlertDialogStyle)
//        val view = layoutInflater.inflate(R.layout.fragment_question_add_dialog, null)
        bottomSheetDialog.setContentView(R.layout.fragment_question_add_dialog)
        Log.d("in on create dialog", "$context")
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("in on view created ", "dialog fragment")
    }
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
//    }

}