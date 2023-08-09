package com.example.quizbanktest.adapters.quiz

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class LinearLayoutWrapper(context: Context?) : LinearLayoutManager(context) {
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}