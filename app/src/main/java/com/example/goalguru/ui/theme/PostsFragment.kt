package com.example.goalguru.ui.theme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.R

class PostsFragment : Fragment() {

    private var postType: String? = null

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

        val posts = if (postType == "your_posts") {
            getYourPosts()
        } else {
            getFriendsPosts()
        }

        val adapter = PostAdapter(posts)
        recyclerView.adapter = adapter

        return view
    }

    private fun getYourPosts(): List<Post> {
        return listOf(
            Post("Meir Cohen", "Hello everyone!", listOf(), 5),
            Post("Meir Cohen", "Check out this cool picture!", listOf("https://example.com/image2.jpg"), 10)
        )
    }
    private fun getFriendsPosts(): List<Post> {
        return listOf(
            Post("John Doe", "Hello everyone!", listOf(), 5),
            Post("Liraz Cohen", "Check out this cool picture!", listOf("https://example.com/image2.jpg"), 10)
        )
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
