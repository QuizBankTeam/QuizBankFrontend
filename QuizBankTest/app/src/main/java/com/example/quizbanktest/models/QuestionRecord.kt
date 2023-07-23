package com.example.quizbanktest.models

import android.os.Parcel
import android.os.Parcelable

data class QuestionRecord(val _id: String,
                          val user: String,
                          var userAnswerOptions: ArrayList<String>?,
                          var userAnswerDescription: String,
                          var correct: Boolean?, //correct or incorrect
                          val date: String,
                          val question: String, // question id
                          val quizRecord: String): Parcelable // quizRecord id)
{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()?.toCollection(ArrayList()),
        parcel.readString()!!,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(user)
        parcel.writeStringList(userAnswerOptions)
        parcel.writeString(userAnswerDescription)
        parcel.writeValue(correct)
        parcel.writeString(date)
        parcel.writeString(question)
        parcel.writeString(quizRecord)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionRecord> {
        override fun createFromParcel(parcel: Parcel): QuestionRecord {
            return QuestionRecord(parcel)
        }

        override fun newArray(size: Int): Array<QuestionRecord?> {
            return arrayOfNulls(size)
        }
    }
}
