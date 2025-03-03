package com.example.travelapp.model

import android.os.Parcel
import android.os.Parcelable

data class BookingModel(
    var bookingId : String = "",
    var userId: String = "",

    var guideName: String = "",
    var guideEmail : String = "",
    var bookingDate : String = "",

    ) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookingId)
        parcel.writeString(userId)
        parcel.writeString(guideName)
        parcel.writeString(guideEmail)
        parcel.writeString(bookingDate)


    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingModel> {
        override fun createFromParcel(parcel: Parcel): BookingModel {
            return BookingModel(parcel)
        }

        override fun newArray(size: Int): Array<BookingModel?> {
            return arrayOfNulls(size)
        }
    }
}