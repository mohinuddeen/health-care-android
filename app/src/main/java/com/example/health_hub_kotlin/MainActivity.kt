package com.example.health_hub_kotlin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.health_hub_kotlin.databinding.ActivityMainBinding
import com.example.health_hub_kotlin.ui.bookings.BookingsFragment
import com.example.health_hub_kotlin.ui.home.HomeFragment
import com.example.health_hub_kotlin.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //create light status bar
        // Set status bar color to white
        window.statusBarColor = getColor(android.R.color.white)

        // Make status bar icons dark (for light background)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true



        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_bookings -> {
                    // TODO: Implement BookingsFragment
                    loadFragment(BookingsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    // TODO: Implement ProfileFragment
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // Set default selection
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}