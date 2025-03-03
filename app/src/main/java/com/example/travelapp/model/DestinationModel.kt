package com.example.travelapp.model

import android.os.Parcel
import android.os.Parcelable

data class DestinationModel(
    var destinationId : String = "",
    var userId: String = " ",
    var title : String = "",
    var desc : String = "",
    var date: String = "",
    var location : String = "",
    var imageUri: String = "", // URI of the uploaded image (from Firebase or device storage)
    var imageName: String = ""

    ) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(destinationId)
        parcel.writeString(userId)
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeString(imageUri)
        parcel.writeString(imageName)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DestinationModel> {
        override fun createFromParcel(parcel: Parcel): DestinationModel {
            return DestinationModel(parcel)
        }

        override fun newArray(size: Int): Array<DestinationModel?> {
            return arrayOfNulls(size)
        }
    }
}