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
    private var posts: List<Post> = listOf()
    private var adapter: PostAdapter? = null

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

        if (postType == "your_posts") {
            getYourPosts()
        } else {
            this.posts = getFriendsPosts()
        }

        adapter = PostAdapter(this.posts)
        recyclerView.adapter = adapter

        return view
    }

    private fun getYourPosts() {
        Model.shared.getPosts {
             this.posts = it
            adapter?.set(it)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun getFriendsPosts(): List<Post> {
        return listOf(
            Post(3, "John Doe", "Hello everyone!", 5),
            Post(4, "Liraz Cohen", "Check out this cool picture!", 10)
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
