package com.example.goalguru

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity
import com.example.goalguru.ui.theme.PostAdapter

class PostsViewModel : ViewModel() {
    private var _posts: MutableList<Post>? = null
    private var adapter: PostAdapter? = null

    fun setAdapter(adapter: PostAdapter) {
        this.adapter = adapter
    }
    var posts: MutableList<Post>?
        get() = _posts
        set(value) {
            _posts = value
            adapter?.set(value ?: mutableListOf())
        }

    fun add(index: Int, post: Post) {
        try {
        val posts = this.posts?.toMutableList()


        posts?.add(index, post)
        this.posts = posts

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updatePosts(posts: MutableList<Post>) {
        this.posts = posts.sortedByDescending { it.timestamp }.toMutableList()
    }
}