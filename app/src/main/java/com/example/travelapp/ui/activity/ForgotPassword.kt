package com.example.travelapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityForgotPasswordBinding
import com.example.travelapp.databinding.ActivityRegisterBinding
import com.example.travelapp.model.UserModel
import com.example.travelapp.repository.UserRepositoryImpl
import com.example.travelapp.ui.activity.RegisterActivity
import com.example.travelapp.utils.LoadingUtils
import com.example.travelapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding

    lateinit var userViewModel: UserViewModel

    lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepositoryImpl(FirebaseAuth.getInstance())

        loadingUtils = LoadingUtils(this)

        binding.forgotButton.setOnClickListener({
            val email: String = binding.forgotEmail.text.toString()
            userViewModel.forgetPassword(email){
                    success,message ->
                if(success){
                    Toast.makeText(this@ForgotPassword
                        ,message,Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@ForgotPassword
                        ,message,Toast.LENGTH_SHORT).show()
                }}
            val intent = Intent(this@ForgotPassword,
                LoginActivity::class.java)
            startActivity(intent)
            })

        userViewModel = UserViewModel(userRepository)
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}