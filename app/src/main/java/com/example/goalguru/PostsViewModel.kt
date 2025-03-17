package com.example.goalguru

import androidx.lifecycle.ViewModel
import com.example.goalguru.model.Post

class PostsViewModel : ViewModel() {
    private var _posts: MutableList<Post>? = null

    var posts: MutableList<Post>?
        get() = _posts
        set(value) {
            _posts = value
        }

    fun add(index: Int, post: Post) {
        val posts = this.posts?.toMutableList()
        posts?.add(index, post)
        this.posts = posts
    }

    fun updatePosts(posts: MutableList<Post>) {
        this.posts = posts
    }
}