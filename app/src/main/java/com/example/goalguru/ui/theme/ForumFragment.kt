package com.example.goalguru.ui.theme

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.goalguru.R
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.UUID


class ForumFragment : Fragment() {

    private val selectedImageUris = mutableListOf<String>()
    private var currentImageIndex = 0
    private var postDialog: Dialog? = null

    // Register for activity result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Convert URI to string for storage
            val uriString = it.toString()
            selectedImageUris.add(uriString)

            // Update the preview in the dialog
            updateImagePreview()
        }
    }

    private fun findPostsFragment(type: String): PostsFragment? {
        val viewPager = view?.findViewById<ViewPager2>(R.id.viewPager)
        val adapter = viewPager?.adapter as? ForumPagerAdapter

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
            tab.text = if (position == 0) "Your Posts" else "Explore"
        }.attach()

        // Set up FAB click listener
        fabAddPost.setOnClickListener {
            showCreatePostDialog()
        }

        return view
    }

    private fun showCreatePostDialog() {
        // Reset state for new dialog
        selectedImageUris.clear()
        currentImageIndex = 0

        postDialog = Dialog(requireContext())
        postDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        postDialog?.setContentView(R.layout.dialog_create_post)
        postDialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val etPostText: EditText = postDialog?.findViewById(R.id.et_post_text)!!
        val btnAddImage: Button = postDialog?.findViewById(R.id.btn_add_image)!!
        val btnCancel: Button = postDialog?.findViewById(R.id.btn_cancel)!!
        val btnPost: Button = postDialog?.findViewById(R.id.btn_post)!!

        // Set up add image button
        btnAddImage.setOnClickListener {
            if (selectedImageUris.size < 3) {
                getContent.launch("image/*")
            } else {
                Toast.makeText(context, "Maximum 3 images allowed", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up cancel button
        btnCancel.setOnClickListener {
            postDialog?.dismiss()
        }

        // Set up post button
        btnPost.setOnClickListener {
            val postText = etPostText.text.toString().trim()

            if (postText.isEmpty()) {
                Toast.makeText(context, "Please enter text for your post", Toast.LENGTH_SHORT).show()
            }
            if(postText.length > 200) {
                Toast.makeText(context, "text length must be under 200 characters",Toast.LENGTH_SHORT).show()

            } else {
                // Create and add the new post
                createNewPost(postText, selectedImageUris)
                postDialog?.dismiss()
            }
        }

        postDialog?.show()
    }

    private fun updateImagePreview() {
        if (postDialog != null && postDialog?.isShowing == true) {
            val imagePreview1: ImageView = postDialog?.findViewById(R.id.image_preview_1)!!
            val imagePreview2: ImageView = postDialog?.findViewById(R.id.image_preview_2)!!
            val imagePreview3: ImageView = postDialog?.findViewById(R.id.image_preview_3)!!

            // Update visibility and content based on selected images
            if (selectedImageUris.isNotEmpty()) {
                imagePreview1.visibility = View.VISIBLE
                imagePreview1.setImageURI(Uri.parse(selectedImageUris[0]))
            }

            if (selectedImageUris.size > 1) {
                imagePreview2.visibility = View.VISIBLE
                imagePreview2.setImageURI(Uri.parse(selectedImageUris[1]))
            }

            if (selectedImageUris.size > 2) {
                imagePreview3.visibility = View.VISIBLE
                imagePreview3.setImageURI(Uri.parse(selectedImageUris[2]))
            }
        }
    }

    private fun createNewPost(text: String, imageUrls: List<String>) {
        try {

            // Use the findPostsFragment function to get the fragment
            val yourPostsFragment = findPostsFragment("your_posts")
            yourPostsFragment?.addNewPost(text, imageUrls)

            // Show success message
            Toast.makeText(context, "Post created successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("ForumFragment", "Error creating post", e)
            Toast.makeText(context, "Error creating post", Toast.LENGTH_SHORT).show()
        }
    }
}