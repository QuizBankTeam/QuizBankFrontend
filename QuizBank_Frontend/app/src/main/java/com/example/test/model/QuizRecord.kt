package com.example.test.model

import android.os.Parcel
import android.os.Parcelable

data class QuizRecord(
    val id: String,
    val title: String,
    val quizId: String,
    val date: String,
    val type: String, //casual, single
    val totalScore:Int,
    val duringTime: Int?,
    val startDate: String,
    val endDate: String,
    val members: ArrayList<String>?, // user id
    val questionRecords: ArrayList<String>) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()?.toCollection(ArrayList()),
        parcel.createStringArrayList()!!.toCollection(ArrayList()),
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(quizId)
        parcel.writeString(date)
        parcel.writeString(type)
        parcel.writeInt(totalScore)
        parcel.writeValue(duringTime)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
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
