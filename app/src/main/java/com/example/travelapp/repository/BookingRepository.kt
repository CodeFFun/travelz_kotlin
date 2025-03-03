package com.example.travelapp.repository

import com.example.travelapp.model.BookingModel
import com.google.firebase.auth.FirebaseUser

interface BookingRepository {

    fun addBookingToDatabase(userId:String,bookingModel: BookingModel,
                          callback: (Boolean, String) -> Unit)

    fun getCurrentUser() : FirebaseUser?

    fun getBookingFromDatabase(userId:String,
                            callback: (List<BookingModel>?, Boolean, String)
                            -> Unit)


    fun editBooking(userId: String,bookingId: String,data:MutableMap<String,Any>,
                    callback: (Boolean, String) -> Unit)

    fun deleteBooking(userId: String,bookingId: String,
                    callback: (Boolean, String) -> Unit)
}