package com.example.goalguru

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity
import com.example.goalguru.model.dao.AppLocalDb

class PostsViewModel : ViewModel() {
    private var _posts: MutableList<Post>? = null

    var posts: MutableList<Post>?
        get() = _posts
        set(value) {
            _posts = value
        }

    fun add(index: Int, post: PostEntity) {
        try {
        val posts = this.posts?.toMutableList()


        posts?.add(index, Post(
            id = post.id,
            userId = post.userId,
            text = post.text,
            imageUrls = post.imageUrls,
            timestamp = post.timestamp
        ))
        this.posts = posts

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updatePosts(posts: MutableList<Post>) {
        Log.d("post: ", "posts: ${posts}")

        this.posts = posts
    }
}