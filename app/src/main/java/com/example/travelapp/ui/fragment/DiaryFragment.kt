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
import com.example.travelapp.adapter.DestinationAdapter
import com.example.travelapp.databinding.FragmentDiaryBinding
import com.example.travelapp.repository.DestinationRepositoryImpl
import com.example.travelapp.ui.activity.AddDestination
import com.example.travelapp.ui.activity.UpdateDestination
import com.example.travelapp.viewmodel.DestinationViewModel
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding

    lateinit var destinationViewModel: DestinationViewModel

    lateinit var destinationAdapter: DestinationAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        var repo = DestinationRepositoryImpl(FirebaseAuth.getInstance())
        destinationViewModel = DestinationViewModel(repo)

        binding?.destinationFloating?.setOnClickListener{
            val intent = Intent(requireContext(),
                AddDestination::class.java)
            startActivity(intent)
        }



        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (userId.isEmpty()) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }

        destinationAdapter = DestinationAdapter(mutableListOf(),  onDeleteClicked = { destinationId -> deleteDestination(destinationId) },
            onUpdate = { destinationId -> updateDestination(destinationId) })

        binding?.recyclerDiary?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerDiary?.adapter = destinationAdapter

        destinationViewModel.destinationList.observe(viewLifecycleOwner, Observer {
            destination -> if(destination.isNotEmpty()){
            destinationAdapter.updateData(destination)
        } else {
            Log.e("DestinationList", "No destination found")
        }
        })

        destinationViewModel.getDestinations(userId)

        return binding?.root
    }

    private fun deleteDestination(destinationId: String) {
        // Handle the deletion logic here
        destinationViewModel.deleteDestination(FirebaseAuth.getInstance().currentUser?.uid ?: "", destinationId)
        Toast.makeText(context, "Destination deleted!", Toast.LENGTH_SHORT).show()
    }

    private fun updateDestination(destinationId: String) {
        val intent = Intent(requireContext(), UpdateDestination::class.java)
        intent.putExtra("destinationId", destinationId)  // Pass the destinationId to the update activity
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}