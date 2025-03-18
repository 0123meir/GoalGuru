package com.example.goalguru

import androidx.lifecycle.ViewModel
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity

class PostsViewModel : ViewModel() {
    private var _posts: MutableList<Post>? = null

    var posts: MutableList<Post>?
        get() = _posts
        set(value) {
            _posts = value
        }

    fun add(index: Int, post: PostEntity) {
        val posts = this.posts?.toMutableList()

        var post = Post(
            id = post.id,
            userId = post.userId,
            text = post.text,
            imageUrls = post.imageUrls,
            timestamp = post.timestamp
        )

        posts?.add(index, post)
        this.posts = posts
    }

    fun updatePosts(posts: MutableList<Post>) {
        this.posts = posts
    }
}