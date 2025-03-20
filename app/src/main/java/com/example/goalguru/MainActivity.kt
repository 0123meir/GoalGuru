package com.example.goalguru

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.lottie.LottieAnimationView
import com.example.goalguru.databinding.ActivityMainBinding
import com.google.android.material.imageview.ShapeableImageView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var loadingAnimation: LottieAnimationView
    private val loadingViewModel: LoadingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingAnimation = binding.loadingAnimation

        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Make sure the start destination is set to todoListFragment
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.todoListFragment)
        navController.graph = navGraph

        // Observe data loading state
        loadingViewModel.isDataLoaded.observe(this) { isLoaded ->
            loadingAnimation.visibility = if (isLoaded) View.GONE else View.VISIBLE
        }

        // Set up header navigation
        setupHeaderNavigation()
    }

    private fun setupHeaderNavigation() {
        // Get references to header views
        val todoListButton = findViewById<ImageView>(R.id.iv_todo_list)
        val profilePhoto = findViewById<ShapeableImageView>(R.id.iv_profile_photo)
        val forumButton = findViewById<ImageView>(R.id.iv_forum)

        // Navigate to to-do List
        todoListButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.todoListFragment) {
                loadingViewModel.setDataLoaded(false) // Show loading
                navController.popBackStack(R.id.todoListFragment, false)
            }
        }

        // Navigate to Profile
        profilePhoto.setOnClickListener {
            if (navController.currentDestination?.id != R.id.profileFragment) {
                when (navController.currentDestination?.id) {
                    R.id.todoListFragment -> navController.navigate(R.id.action_todo_to_profile)
                    R.id.forumFragment -> navController.navigate(R.id.action_forum_to_profile)
                }
            }
        }

        // Navigate to Forum
        forumButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.forumFragment) {
                loadingViewModel.setDataLoaded(false) // Show loading
                when (navController.currentDestination?.id) {
                    R.id.todoListFragment -> navController.navigate(R.id.action_todo_to_forum)
                    R.id.profileFragment -> navController.navigate(R.id.action_profile_to_forum)
                }
            }
        }
        }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}