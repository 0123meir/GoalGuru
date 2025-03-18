package com.example.goalguru.ui.theme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.PostsViewModel
import com.example.goalguru.R

import com.example.goalguru.model.Model
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity

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

        getYourPosts()

        return view
    }


    private fun getYourPosts() {
        Model.shared.getPosts {
            viewModel?.updatePosts(it)
            postAdapter?.set(it)
            postAdapter?.notifyDataSetChanged()
        }
    }

    fun addNewPost(post: PostEntity) {
        viewModel?.add(0, post)  // Add to the beginning of the list
        postAdapter?.notifyItemInserted(0)
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