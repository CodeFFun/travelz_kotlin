package com.example.travelapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import com.example.travelapp.databinding.ActivityMainBinding
import com.example.travelapp.ui.fragment.BookingFragment
import com.example.travelapp.ui.fragment.DiaryFragment
import com.example.travelapp.ui.fragment.HomeFragment
import com.example.travelapp.ui.fragment.ProfileFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId) {
                R.id.homeNav -> replaceFragment(HomeFragment())
                R.id.diaryNav -> replaceFragment(DiaryFragment())
                R.id.profileNav -> replaceFragment(ProfileFragment())
                R.id.bookingNav -> replaceFragment(BookingFragment())

                else -> {


                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}
