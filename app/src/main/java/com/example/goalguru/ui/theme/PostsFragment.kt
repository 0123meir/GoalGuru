package com.example.goalguru.ui.theme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.R
import com.example.goalguru.util.MockDataProvider

class PostsFragment : Fragment() {

    private var postType: String? = null
    private var posts: MutableList<Post> = mutableListOf()
    private var postAdapter: PostAdapter? = null

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
        val view = inflater.inflate(R.layout.fragment_posts, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        posts = if (postType == "your_posts") {
            getYourPosts().toMutableList()
        } else {
            getFriendsPosts().toMutableList()
        }

        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter

        return view
    }

    private fun getYourPosts(): List<Post> {
        return MockDataProvider.generateMockPosts(3)
    }

    private fun getFriendsPosts(): List<Post> {
        return MockDataProvider.generateMockPosts(10)
    }

    fun addNewPost(post: Post) {
        posts.add(0, post)  // Add to the beginning of the list
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