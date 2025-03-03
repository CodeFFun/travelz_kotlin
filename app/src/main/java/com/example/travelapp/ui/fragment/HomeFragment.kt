package com.example.travelapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.adapter.UserAdapter
import com.example.travelapp.databinding.FragmentHomeBinding
import com.example.travelapp.databinding.FragmentProfileBinding
import com.example.travelapp.model.UserModel
import com.example.travelapp.repository.UserRepositoryImpl
import com.example.travelapp.utils.LoadingUtils
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

    lateinit var userAdapter: UserAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater,container, false)

        var repo = UserRepositoryImpl(FirebaseAuth.getInstance())
        userViewModel = UserViewModel(repo)

        userAdapter = UserAdapter(mutableListOf())

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}