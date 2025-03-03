package com.example.travelapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.travelapp.MainActivity
import com.example.travelapp.R
import com.example.travelapp.databinding.FragmentProfileBinding
import com.example.travelapp.repository.UserRepositoryImpl
import com.example.travelapp.ui.activity.LoginActivity
import com.example.travelapp.ui.activity.RegisterActivity
import com.example.travelapp.utils.LoadingUtils
import com.example.travelapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    lateinit var userViewModel: UserViewModel

    lateinit var loadingUtils: LoadingUtils


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        var repo = UserRepositoryImpl(FirebaseAuth.getInstance())
        userViewModel = UserViewModel(repo)

        var currentUser = userViewModel.getCurrentUser()

        currentUser.let {
            userViewModel.getUserFromDatabase(it?.uid.toString())
        }

        userViewModel.userData.observe(requireActivity()){users->
            binding.profileEmail.text = users?.email
            binding.profileName.setText(users?.fullName)
            binding.profileAddress.setText(users?.address)
            binding.profilePhone.setText(users?.phoneNumber)
            binding.spinner.setTag(users?.userType)

        }

        val role = listOf<String>("Traveler", "Guide")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_list, role)
        val spinner = binding.spinner
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                p2: Int,
                p3: Long
            ) {
                binding.spinner.setTag(role[p2])
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        loadingUtils = LoadingUtils(requireActivity())

        binding.logout.setOnClickListener({
            userViewModel.logout{ success,message ->
                if(success){
                    Toast.makeText(requireContext()
                        ,message,Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext()
                        ,message,Toast.LENGTH_SHORT).show()
                }}
            val intent = Intent(requireContext(),
                LoginActivity::class.java)
            startActivity(intent)
        })

        binding.profileUpdate.setOnClickListener({
            loadingUtils.show()

            var fullName :String = binding.profileName.text.toString()
            var userType : String = binding.spinner.tag.toString()
            var address : String = binding.profileAddress.text.toString()
            var phoneNumber: String = binding.profilePhone.text.toString()

            val data = mutableMapOf<String, Any>()
            data["fullName"] = fullName
            data["userType"] = userType
            data["address"] = address
            data["phoneNumber"] = phoneNumber

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if(userId != null){
                userViewModel.editProfile(userId, data){
                        success,message->
                    if(success){
                        Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
                        loadingUtils.dismiss()
                    }else{
                        Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
                        loadingUtils.dismiss()

                    }
                }
            }


        })

        // Return the root view of the fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}