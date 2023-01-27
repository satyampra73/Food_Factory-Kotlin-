package com.example.foodfactory.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

data class Bill(
    val date: String = "",
    val paymentStatus: Boolean = false,
    val amt: Double = 0.00,
    val orderId: String = "",
    val uid: String = "",
    val payMethod: String = "upi",
    var approved: String = ""
) : Parcelable {
    @SuppressLint("NewApi")
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readBoolean(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (paymentStatus) 1 else 0)
        parcel.writeDouble(amt)
        parcel.writeString(orderId)
        parcel.writeString(uid)
        parcel.writeString(payMethod)
        parcel.writeString(approved)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bill> {
        override fun createFromParcel(parcel: Parcel): Bill {
            return Bill(parcel)
        }

        override fun newArray(size: Int): Array<Bill?> {
            return arrayOfNulls(size)
        }
    }
}