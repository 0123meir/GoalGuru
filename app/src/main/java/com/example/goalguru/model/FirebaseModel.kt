package com.example.goalguru.model

import com.example.goalguru.utils.extensions.toFirebaseTimestamp
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import java.util.Date
import java.util.UUID

typealias PostsCallback = (List<PostEntity>) -> Unit

class FirebaseModel {

    private val database = Firebase.firestore

    init {
        val setting = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }

        database.firestoreSettings = setting
    }


    fun getPosts(sinceLastUpdated: Long, callback: PostsCallback) {
        database.collection("posts")
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED, sinceLastUpdated)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<PostEntity> = mutableListOf()
                        for (json in it.result) {
                            val userId = json.getString("userId") ?: ""
                            val text = json.getString("text") ?: ""
                            val imageUrls = json.get("imageUrls") as List<String>
                            val timestamp = json.getLong("timestamp") ?: 0L

                            posts.add(PostEntity(
                                id = UUID.randomUUID().toString(),
                                userId = userId,
                                text = text,
                                imageUrls = imageUrls,
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
            "username" to post.username,
            "text" to post.text,
            "imageUrls" to post.imageUrls,
            "likes" to post.likesCount,
            "likedByUser" to post.isLikedByUser,
            "comments" to post.comments,
            "timestamp" to post.timestamp
        )

        database.collection("posts").add(postMap)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }
}
