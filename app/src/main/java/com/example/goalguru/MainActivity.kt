package com.example.goalguru

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.goalguru.ui.theme.ForumFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Handle bottom navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
//                R.id.nav_login -> replaceFragment(LoginFragment())
//                R.id.nav_register -> replaceFragment(RegisterFragment())
                R.id.nav_forum -> replaceFragment(ForumFragment())
            }
            true
        }
    }

    // Function to replace fragments
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}