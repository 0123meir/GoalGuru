package com.example.goalguru.ui.theme

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.goalguru.R
import com.example.goalguru.model.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ForumFragment : Fragment(), PostDialogHandler.PostDialogCallback {

    private lateinit var dialogHandler: PostDialogHandler

    // Register for activity result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            dialogHandler.addImage(it)
        }
    }

    private fun findPostsFragment(type: String): PostsFragment? {
        val fragments = childFragmentManager.fragments
        return fragments.filterIsInstance<PostsFragment>()
            .find { it.getPostType() == type }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment_forum layout
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        val fabAddPost: FloatingActionButton = view.findViewById(R.id.fab_add_post)

        val adapter = ForumPagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Your Posts" else "Friends"
        }.attach()

        // Initialize dialog handler
        dialogHandler = PostDialogHandler(requireContext())

        // Set up FAB click listener
        fabAddPost.setOnClickListener {
            dialogHandler.showCreatePostDialog(getContent, this)
        }

        return view
    }

    fun editPost(post: Post, position: Int) {
        dialogHandler.showEditPostDialog(post, getContent, object : PostDialogHandler.PostDialogCallback {
            override fun onPostSubmitted(text: String, imageUris: List<String>) {
                // Update the post
                post.text = text
                post.imageUrls = imageUris

                // Update the UI
                val yourPostsFragment = findPostsFragment("your_posts")
                yourPostsFragment?.updatePost(position)

                Toast.makeText(context, "Post updated successfully!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPostSubmitted(text: String, imageUris: List<String>) {
        createNewPost(text, imageUris)
    }

    private fun createNewPost(text: String, imageUrls: List<String>) {
        try {
            //TODO: save to DB
            val newPost = Post(
                userId = "current_user_id",
                userName = "Current User",
                userProfile = null,
                text = text,
                imageUrls = imageUrls,
                timestamp = System.currentTimeMillis()
            )

            // Use the findPostsFragment function to get the right fragment
            val yourPostsFragment = findPostsFragment("your_posts")
            yourPostsFragment?.addNewPost(newPost)

            // Show success message
            Toast.makeText(context, "Post created successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("ForumFragment", "Error creating post", e)
            Toast.makeText(context, "Error creating post", Toast.LENGTH_SHORT).show()
        }
    }
}