package com.example.quizbanktest.models
import android.os.Parcel
import android.os.Parcelable

data class Question(
    var _id: String?,
    var title: String?,
    var number: String?,//題號
    var description: String?,
    var options: ArrayList<String>?,
    var questionType: String?, // Filling, MultipleChoiceS, ShortAnswer, MultipleChoiceM, TrueOrFalse: true, false
    var bankType: String?,
    var questionBank: String?, //questionBank id
    var answerOptions: ArrayList<String>?,
    var answerDescription: String?,
    var provider: String?, // user id
    val originateFrom: String?,
    var image: ArrayList<String>?, // base64
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
        parcel.readString(),
        parcel.createStringArrayList()?.toCollection(ArrayList()),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        null,
        parcel.createStringArrayList()?.toCollection(ArrayList()),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(title)
        parcel.writeString(number)
        parcel.writeString(description)
        parcel.writeStringList(options)
        parcel.writeString(questionType)
        parcel.writeString(bankType)
        parcel.writeString(questionBank)
        parcel.writeStringList(answerOptions)
        parcel.writeString(answerDescription)
        parcel.writeString(provider)
        parcel.writeString(originateFrom)
        null
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
