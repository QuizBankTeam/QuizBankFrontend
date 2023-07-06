package com.example.test.model

import android.os.Parcel
import android.os.Parcelable

data class Question(
    var id: String?,
    var title: String?,
    var number: String?,//題號
    var description: String?,
    var options: ArrayList<String>?,
    var type: String?, // Filling, MultipleChoiceS, ShortAnswer, MultipleChoiceM, TrueOrFalse: true, false
    var questionBank: String?, //questionBank id
    var answerOption: ArrayList<String>?,
    var answerDescription: String?,
    var provider: String?, // user id
    var image: Int, // base64
    var tag: ArrayList<String>?,
    var createdDate: String?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()?.toCollection(ArrayList()),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()?.toCollection(ArrayList()),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.createStringArrayList()?.toCollection(ArrayList()),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(number)
        parcel.writeString(description)
        parcel.writeStringList(options)
        parcel.writeString(type)
        parcel.writeString(questionBank)
        parcel.writeStringList(answerOption)
        parcel.writeString(answerDescription)
        parcel.writeString(provider)
        parcel.writeInt(image)
        parcel.writeStringList(tag)
        parcel.writeString(createdDate)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
