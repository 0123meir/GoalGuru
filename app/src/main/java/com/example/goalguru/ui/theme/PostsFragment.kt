package com.example.goalguru.ui.theme

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.LoadingViewModel
import com.example.goalguru.PostsViewModel
import com.example.goalguru.R
import com.example.goalguru.model.Model
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity

class PostsFragment : Fragment(), PostDialogHandler.PostDialogCallback {

    private var postType: String? = null
    private lateinit var postAdapter: PostAdapter
    private var viewModel: PostsViewModel? = null
    private lateinit var dialogHandler: PostDialogHandler
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            dialogHandler.addImage(it)
        }
    }

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

        postAdapter = PostAdapter(viewModel?.posts ?: mutableListOf(), this)
        viewModel?.setAdapter(postAdapter)
        recyclerView.adapter = postAdapter

        dialogHandler = PostDialogHandler(requireContext())

        loadingViewModel.setDataLoaded(false) // Show loading
        getAllPosts()

        return view
    }

    private fun getAllPosts() {
        Model.shared.getPosts {
            viewModel?.updatePosts(it)
            postAdapter?.notifyDataSetChanged()
            loadingViewModel.setDataLoaded(true) // Hide loading
        }
    }

    fun addNewPost() {
        dialogHandler.showPostDialog(null, getContent, this)
    }

    fun editPost(updatedPost: Post, position: Int) {
        val postEntity = PostEntity(
            id = updatedPost.id,
            userId = updatedPost.userId,
            text = updatedPost.text,
            imageUrls = updatedPost.imageUrls,
            timestamp = updatedPost.timestamp ?: System.currentTimeMillis()
        )
        dialogHandler.showPostDialog(updatedPost, getContent, object : PostDialogHandler.PostDialogCallback {
            override fun onPostSubmitted(post: Post) {
                Model.shared.updatePost(postEntity) { success ->
                    if (success) {
                        viewModel?.posts?.set(position, post)
                        postAdapter?.notifyItemChanged(position)
                        Toast.makeText(context, "Post updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to update post", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onPostSubmitted(post: Post) {
        viewModel?.add(0, post)
        scrollToTop()

        Model.shared.addPost(post) { success ->
            if (!success) {
                // Remove the post if the operation failed
                viewModel?.posts?.removeAt(0)
                postAdapter.notifyItemRemoved(0)
                Toast.makeText(context, "Failed to add post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getPostType(): String? {
        return postType
    }

    fun scrollToTop() {
        val recyclerView: RecyclerView? = view?.findViewById(R.id.recyclerView)
        recyclerView?.smoothScrollToPosition(0)
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