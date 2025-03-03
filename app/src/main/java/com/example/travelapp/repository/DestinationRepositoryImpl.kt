package com.example.travelapp.repository

import com.example.travelapp.model.DestinationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DestinationRepositoryImpl(auth : FirebaseAuth) : DestinationRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference.child("destinations")

    override fun addDestinationToDatabase(
        userId: String,
        destinationModel: DestinationModel,
        callback: (Boolean, String) -> Unit
    ) {
        val destinationId = reference.child(userId).push().key // Generate unique ID
        if (destinationId != null) {
            destinationModel.destinationId = destinationId
            reference.child(userId).child(destinationId).setValue(destinationModel)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Destination added successfully")
                    } else {
                        callback(false, task.exception?.message ?: "Failed to add destination")
                    }
                }
        } else {
            callback(false, "Failed to generate destination ID")
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun getDestination(
        destinationId: String,
        callback: (List<DestinationModel>?, Boolean, String) -> Unit
    ) {
        // Get reference to the destination in the database
        val destinationRef = FirebaseDatabase.getInstance().reference
            .child("destinations")
            .child(destinationId)

        destinationRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val destination = task.result?.getValue(DestinationModel::class.java)
                if (destination != null) {
                    // Wrap the single destination in a list and pass it to the callback
                    callback(listOf(destination), true, "Destination fetched successfully")
                } else {
                    callback(null, false, "Destination not found")
                }
            } else {
                callback(null, false, "Error fetching destination: ${task.exception?.message}")
            }
        }
    }


    override fun getDestinationFromDatabase(
        userId: String,
        callback: (List<DestinationModel>?, Boolean, String) -> Unit
    ) {
        reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val destinationList = mutableListOf<DestinationModel>()

                if (snapshot.exists()) {
                    for (destinationSnapshot in snapshot.children) {
                        val model = destinationSnapshot.getValue(DestinationModel::class.java)
                        if (model != null) {
                            destinationList.add(model)
                        }
                    }
                    callback(destinationList, true, "Destinations fetched successfully")
                } else {
                    callback(emptyList(), false, "No destinations found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun editDestination(
        userId: String,
        destinationId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId).child(destinationId).updateChildren(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Destination updated successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update destination")
                }
            }
    }

    override fun deleteDestination(
        userId: String,
        destinationId: String,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId).child(destinationId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Destination deleted successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to delete destination")
                }
            }
    }
}