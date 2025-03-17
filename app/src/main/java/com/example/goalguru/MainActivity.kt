package com.example.goalguru

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.goalguru.ui.theme.ForumFragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.FirebaseApp

//TODO: add when fragments are implemented
//import com.example.goalguru.ui.theme.ProfileFragment
import com.example.goalguru.ui.theme.TodoListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var rightIcon: ImageView
    private lateinit var exitIcon: ImageView
    private lateinit var profilePhoto: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // Initialize views
        val headerView = findViewById<androidx.cardview.widget.CardView>(R.id.header)
        rightIcon = headerView.findViewById(R.id.iv_right_icon)
        exitIcon = headerView.findViewById(R.id.iv_exit)
        profilePhoto = headerView.findViewById(R.id.iv_profile_photo)

        // Set the initial fragment (ForumFragment) if the activity is newly created
        if (savedInstanceState == null) {
            loadFragment(ForumFragment())
        }

        // Set click listeners
        setupClickListeners()
    }

    private fun setupClickListeners() {
        exitIcon.setOnClickListener {
            AppUtils.showExitConfirmationDialog(this) {
                finish()
            }
        }
        profilePhoto.setOnClickListener {
//            loadFragment(ProfileFragment())
        }
    }

    // Function to load a fragment into the fragment_container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // Update the right icon based on the loaded fragment
        updateRightIcon(fragment)
    }

    private fun updateRightIcon(fragment: Fragment) {
        if (fragment is ForumFragment) {
            rightIcon.setImageResource(R.drawable.to_do_list)
            rightIcon.setOnClickListener {
                loadFragment(TodoListFragment())
            }
        } else if (fragment is TodoListFragment) {
            rightIcon.setImageResource(R.drawable.ic_forum)
            rightIcon.setOnClickListener {
                loadFragment(ForumFragment())
            }
        }
    }
}