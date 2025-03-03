package com.example.travelapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.adapter.UserAdapter
import com.example.travelapp.databinding.FragmentHomeBinding
import com.example.travelapp.model.BookingModel
import com.example.travelapp.repository.UserRepositoryImpl
import com.example.travelapp.ui.activity.UpdateDestination
import com.example.travelapp.viewmodel.BookingViewModel
import com.example.travelapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    lateinit var userViewModel: UserViewModel
    lateinit var bookingViewModel: BookingViewModel

    lateinit var userAdapter: UserAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater,container, false)

        var repo = UserRepositoryImpl(FirebaseAuth.getInstance())
        userViewModel = UserViewModel(repo)

        userAdapter = UserAdapter(mutableListOf()) { userId, title, email, bookingDate ->
            // Create booking when the guide button is clicked
            createBooking(userId, title, email, bookingDate)
        }

        binding?.recyclerHome?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerHome?.adapter = userAdapter

        userViewModel.guideData.observe(viewLifecycleOwner, Observer { guides ->
            if (guides.isNotEmpty()) {
                userAdapter.updateData(guides)
            } else {
                Log.e("GuideList", "No guides found")
            }
        })

        userViewModel.getGuidesFromDatabase()

        return binding?.root
    }

    private fun createBooking(userId: String, guideName: String, guideEmail: String, bookingDate: String) {
        // Create a BookingModel object
        val bookingModel = BookingModel(
            userId = userId,
            guideName = guideName,
            guideEmail = guideEmail,
            bookingDate = bookingDate
        )

        // Call BookingViewModel to add the booking to the database
        bookingViewModel.addBookingToDatabase(userId, bookingModel) { success, message ->
            if (success) {
                Toast.makeText(context, "Booking successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to create booking: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}