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
                            posts.add(Post.fromJSON(json.data))
                        }
                        callback(posts)
                    }
                    false -> callback(mutableListOf())
                }
            }
    }

    fun addPost(post: Post, callback: (Boolean) -> Unit) {
        database.collection("posts").add(post.json)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }
}
