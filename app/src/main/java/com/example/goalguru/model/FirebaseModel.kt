package com.example.goalguru.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings

class FirebaseModel {

    private val database = Firebase.firestore

    init {
        val setting = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }

        database.firestoreSettings = setting
    }

    fun getPosts(callback: (MutableList<Post>) -> Unit) {
        database.collection("posts").get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val userId = json.getString("userId") ?: ""
                            val username = json.getString("username") ?: ""
                            val text = json.getString("text") ?: ""
                            val imageUrls = json.get("imageUrls") as List<String>
                            val likes = json.getLong("likes")?.toInt() ?: 0
                            val likedByUser = json.getBoolean("likedByUser") ?: false
                            val comments = json.get("comments") as MutableList<Comment>
                            val timestamp = json.getLong("timestamp") ?: 0L

                            posts.add(Post(
                                userId = userId,
                                userName = username,
                                text = text,
                                imageUrls = imageUrls,
                                likes = likes,
                                likedByUser = likedByUser,
                                comments = comments,
                                timestamp = timestamp
                            ))
                        }
                        callback(posts)
                    }
                    false -> callback(mutableListOf())
                }
            }
    }

    fun addPost(post: Post, callback: (Boolean) -> Unit) {
        val postMap = hashMapOf(
            "userId" to post.userId,
            "username" to post.userName,
            "text" to post.text,
            "imageUrls" to post.imageUrls,
            "likes" to post.likes,
            "likedByUser" to post.likedByUser,
            "comments" to post.comments,
            "timestamp" to post.timestamp
        )

        database.collection("posts").add(postMap)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }
}
