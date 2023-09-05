package com.example.quizbanktest.models

import android.os.Parcel
import android.os.Parcelable

data class QuizRecord(
    val _id: String,
    val title: String,
    val quizId: String,
    val type: String, //casual, single
    var totalScore:Int,
    val duringTime: Int?,
    val startDateTime: String,
    val endDateTime: String,
    val members: ArrayList<String>, // user id
    val questionRecords: ArrayList<String>) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!.toCollection(ArrayList()),
        parcel.createStringArrayList()!!.toCollection(ArrayList()),
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(title)
        parcel.writeString(quizId)
        parcel.writeString(type)
        parcel.writeInt(totalScore)
        parcel.writeValue(duringTime)
        parcel.writeString(startDateTime)
        parcel.writeString(endDateTime)
        parcel.writeStringList(members)
        parcel.writeStringList(questionRecords)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizRecord> {
        override fun createFromParcel(parcel: Parcel): QuizRecord {
            return QuizRecord(parcel)
        }

        override fun newArray(size: Int): Array<QuizRecord?> {
            return arrayOfNulls(size)
        }
    }

} // question record id)
