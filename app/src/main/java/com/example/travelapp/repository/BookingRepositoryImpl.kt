package com.example.travelapp.repository

import com.example.travelapp.model.BookingModel
import com.example.travelapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingRepositoryImpl(var auth: FirebaseAuth): BookingRepository {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference: DatabaseReference = database.reference.child("booking")

    override fun addBookingToDatabase(
        userId: String,
        bookingModel: BookingModel,
        callback: (Boolean, String) -> Unit
    ) {
        val bookingId = reference.child(userId).push().key

        if (bookingId != null) {
            bookingModel.bookingId = bookingId

            reference.child(userId).child(bookingId).setValue(bookingModel)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(true, "Booking added successfully")
                    } else {
                        callback(false, it.exception?.message.toString())
                    }
                }
        } else {
            callback(false, "Failed to generate booking ID")
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getBookingFromDatabase(
        userId: String,
        callback: (List<BookingModel>?, Boolean, String) -> Unit
    ) {
        reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookingList = mutableListOf<BookingModel>()

                if (snapshot.exists()) {
                    for (bookingSnapshot in snapshot.children) {
                        val model = bookingSnapshot.getValue(BookingModel::class.java)
                        if (model != null) {
                            bookingList.add(model)
                        }
                    }
                    callback(bookingList, true, "Details fetched successfully")
                } else {
                    callback(emptyList(), false, "No bookings found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, error.message)
            }
        })
    }


    override fun editBooking(
        userId: String,
        bookingId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId).child(bookingId).updateChildren(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Booking updated successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update booking")
                }
            }
    }

    override fun deleteBooking(
        userId: String,
        bookingId: String,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId).child(bookingId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Booking deleted successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to delete booking")
                }
            }
    }


}