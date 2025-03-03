package com.example.travelapp.repository


import com.example.travelapp.model.DestinationModel
import com.google.firebase.auth.FirebaseUser

interface DestinationRepository {
    fun addDestinationToDatabase(userId:String,bookingModel: DestinationModel,
                             callback: (Boolean, String) -> Unit)

    fun getCurrentUser() : FirebaseUser?

    fun getDestination(destinationId:String, callback: (List<DestinationModel>?, Boolean, String)
    -> Unit)

    fun getDestinationFromDatabase(userId:String,
                               callback: (List<DestinationModel>?, Boolean, String)
                               -> Unit)


    fun editDestination(userId: String,destinationId: String,data:MutableMap<String,Any>,
                    callback: (Boolean, String) -> Unit)

    fun deleteDestination(userId: String,destinationId: String,
                      callback: (Boolean, String) -> Unit)
}