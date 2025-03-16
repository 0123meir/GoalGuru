package com.example.goalguru.ui.theme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.R

import com.example.goalguru.model.Model
import com.example.goalguru.model.Post

class PostsFragment : Fragment() {
    private var postType: String? = null
    private var posts: MutableList<Post> = mutableListOf()
    private var postAdapter: PostAdapter? = null
    private var forumFragment: ForumFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postType = it.getString(ARG_POST_TYPE)
        }

        // Try to get the parent fragment as ForumFragment
        forumFragment = parentFragment as? ForumFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize with the listener from ForumFragment
        postAdapter = PostAdapter(this.posts, listener = forumFragment)
        recyclerView.adapter = postAdapter

        getYourPosts()

        return view
    }

    private fun getYourPosts() {
        Model.shared.getPosts {
            this.posts = it
            postAdapter?.set(it)
            postAdapter?.notifyDataSetChanged()
        }
    }

    fun addNewPost(post: Post) {
        posts.add(0, post)  // Add to the beginning of the list
        postAdapter?.notifyItemInserted(0)
    }

    fun removePost(position: Int) {
        if (position >= 0 && position < posts.size) {
            posts.removeAt(position)
            postAdapter?.notifyItemRemoved(position)
        }
    }

    fun getPostType(): String? {
        return postType
    }

    fun updatePost(position: Int) {
        postAdapter?.notifyItemChanged(position)
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