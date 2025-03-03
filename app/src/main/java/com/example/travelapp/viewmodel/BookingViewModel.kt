package com.example.travelapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.travelapp.model.BookingModel
import com.example.travelapp.model.UserModel
import com.example.travelapp.repository.BookingRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class BookingViewModel(val repo: BookingRepository) {

    private val _bookingStatus = MutableLiveData<Pair<Boolean, String>>()
    val bookingStatus: LiveData<Pair<Boolean, String>> get() = _bookingStatus

    // Function to add a booking to the database
    fun addBookingToDatabase(userId: String, bookingModel: BookingModel, callback: (Boolean, String) -> Unit) {
        val bookingRef = FirebaseDatabase.getInstance().reference.child("bookings").push()

        // Create a new booking with the given data
        val newBooking = bookingModel.copy(userId = userId)

        // Add booking data to the database
        bookingRef.setValue(newBooking).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Booking created successfully")
            } else {
                callback(false, "Error creating booking: ${task.exception?.message}")
            }
        }
    }

    fun getCurrentUser() : FirebaseUser?{
        return repo.getCurrentUser()
    }


    private var _bookingData = MutableLiveData<List<BookingModel>>()
    val bookingData: LiveData<List<BookingModel>> get() = _bookingData

    private val _operationStatus = MutableLiveData<Pair<Boolean, String>>()
    val operationStatus: LiveData<Pair<Boolean, String>> get() = _operationStatus

    // Fetch bookings for a user from the database
    fun getBookings(userId: String) {
        repo.getBookingFromDatabase(userId) { list, success, message ->
            if (success && list != null) {
                _bookingData.postValue(list)
            } else {
                _bookingData.postValue(emptyList()) // Return empty list if no bookings are found
            }
            _operationStatus.postValue(Pair(success, message))
        }
    }

    fun editBooking(userId: String, bookingId: String, data: MutableMap<String, Any>) {
        repo.editBooking(userId, bookingId, data) { success, message ->
            _operationStatus.postValue(Pair(success, message))
            if (success) {
                getBookings(data["userId"].toString()) // Refresh bookings after update
            }
        }
    }


    // Delete a booking from the database
    fun deleteBooking(userId: String, bookingId: String) {
        repo.deleteBooking(userId, bookingId) { success, message ->
            _operationStatus.postValue(Pair(success, message))
            if (success) {
                getBookings(userId) // Refresh bookings data after deletion
            }
        }
    }
}