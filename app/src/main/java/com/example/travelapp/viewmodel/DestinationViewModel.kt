package com.example.travelapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.travelapp.model.DestinationModel
import com.example.travelapp.repository.DestinationRepository

class DestinationViewModel(val repository : DestinationRepository) {

    private val _destinationList = MutableLiveData<List<DestinationModel>>()
    val destinationList: LiveData<List<DestinationModel>> get() = _destinationList

    private val _operationStatus = MutableLiveData<Pair<Boolean, String>>()
    val operationStatus: LiveData<Pair<Boolean, String>> get() = _operationStatus

    fun addDestination(
        userId: String,
        destinationModel: DestinationModel,
        callback: (Boolean, String) -> Unit
    ) {
        repository.addDestinationToDatabase(userId, destinationModel) { success, message ->
            _operationStatus.postValue(Pair(success, message))
            if (success) {
                getDestinations(userId) // Refresh data
            }
        }
    }

    fun getCurrentUser() : String?{
        return repository.getCurrentUser()?.uid ?: ""
    }

    val destination = MutableLiveData<DestinationModel?>()

    // Fetch destination based on destinationId
    fun getDestination(destinationId: String) {
        repository.getDestination(destinationId) { destinationList, success, message ->
            if (success && !destinationList.isNullOrEmpty()) {
                // Assuming we get only one destination
                destination.postValue(destinationList[0])

            } else {
                destination.postValue(null)
            }
        }
    }

    fun getDestinations(userId: String) {
        repository.getDestinationFromDatabase(userId) { list, success, message ->
            if (success && list != null) {
                _destinationList.postValue(list)
            } else {
                _destinationList.postValue(emptyList())
            }
            _operationStatus.postValue(Pair(success, message))
        }
    }

    fun editDestination(userId: String, destinationId: String, data: MutableMap<String, Any>) {
        repository.editDestination(userId, destinationId, data) { success, message ->
            _operationStatus.postValue(Pair(success, message))
            if (success) {
                getDestinations(userId) // Refresh data
            }
        }
    }

    fun deleteDestination(userId: String, destinationId: String) {
        repository.deleteDestination(userId, destinationId) { success, message ->
            _operationStatus.postValue(Pair(success, message))
            if (success) {
                getDestinations(userId) // Refresh data
            }
        }
    }
}