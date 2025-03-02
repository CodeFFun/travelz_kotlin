package com.example.travelapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityRegisterBinding
import com.example.travelapp.model.UserModel
import com.example.travelapp.repository.UserRepositoryImpl
import com.example.travelapp.utils.LoadingUtils
import com.example.travelapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    lateinit var userViewModel: UserViewModel

    lateinit var loadingUtils: LoadingUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepositoryImpl(FirebaseAuth.getInstance())

        userViewModel = UserViewModel(userRepository)



        loadingUtils = LoadingUtils(this)

        val radioGroup = findViewById<RadioGroup>(R.id.signup_radio)
        var selectedText: String =""

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(checkedId)
                selectedText = selectedRadioButton.text.toString()
                Toast.makeText(this, "Selected: $selectedText", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupButton.setOnClickListener{
            loadingUtils.show()

            var fullName: String = binding.signupName.text.toString()
            var email: String = binding.signupEmail.text.toString()
            var password: String = binding.signupPassword.text.toString()
            val radioGroup = findViewById<RadioGroup>(R.id.signup_radio)
            val selectedRadioButton = radioGroup.checkedRadioButtonId
            val userType: String = if (selectedRadioButton != -1) {
                findViewById<RadioButton>(selectedRadioButton).tag?.toString() ?: "No tage Found"
            } else {
                ""
            }

//            if (!userType.isNullOrEmpty()) {
//                Toast.makeText(this@RegisterActivity, userType, Toast.LENGTH_SHORT).show()
//            }
            userViewModel.signup(email,password){
                    success,message,userId ->
                if(success){
                    val userModel = UserModel(
                        userId,
                        email, fullName, userType,
                    )
                    addUser(userModel)

                }else{
                    loadingUtils.dismiss()
                    Toast.makeText(this@RegisterActivity,
                        message,Toast.LENGTH_SHORT).show()
                }


            }

        }

        binding.signin.setOnClickListener({
            val intent = Intent(this@RegisterActivity,
                LoginActivity::class.java)
            startActivity(intent)
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun addUser(userModel: UserModel){
        userViewModel.addUserToDatabase(userModel.userId,userModel){
                success,message ->
            if(success){
                Toast.makeText(this@RegisterActivity
                    ,message,Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@RegisterActivity
                    ,message,Toast.LENGTH_SHORT).show()
            }
            loadingUtils.dismiss()
        }
    }
}