package com.example.goalguru.ui.theme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.PostsViewModel
import com.example.goalguru.R
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity
import java.util.UUID

class PostsFragment : Fragment() {
    private var postType: String? = null
    private var postAdapter: PostAdapter? = null
    private var viewModel: PostsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postType = it.getString(ARG_POST_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[PostsViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        postAdapter = PostAdapter(viewModel?.posts ?: mutableListOf())

        recyclerView.adapter = postAdapter

        getAllPosts()

        return view
    }


    private fun getAllPosts() {
        Model.shared.getPosts {
            viewModel?.updatePosts(it)
            postAdapter?.set(it)
            postAdapter?.notifyDataSetChanged()
        }
    }

     fun addNewPost(text: String, imageUrls: List<String>) {

        val postId = UUID.randomUUID().toString()

        // Create a Post object
        val newPost = Post(
            id = postId,
            userId = Model.shared.getCurrentUserId(),
            text = text,
            imageUrls = imageUrls,
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            isLikedByUser = false,
            comments = mutableListOf(),
            username = Model.shared.getCurrentUserUsername(),
            userProfilePicture = Model.shared.getCurrentUserImage()
        )

        // Call the add post function
        Model.shared.addPost(newPost) { success ->
            if (success) {
                // Show success message
                Toast.makeText(requireContext(), "Post added successfully", Toast.LENGTH_SHORT).show()

                // Refresh the posts list
                viewModel?.posts?.add(0, newPost)
                postAdapter?.notifyItemInserted(0)
            } else {
                // Show error message
                Toast.makeText(requireContext(), "Failed to add post", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun getPostType(): String? {
        return postType
    }

    companion object {
        private const val ARG_POST_TYPE = "postType"

        fun newInstance(postType: String) = PostsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_POST_TYPE, postType)
            }
        }
    }
}