package com.example.goalguru.model

import UserViewModel
import android.util.Log
import androidx.activity.viewModels
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings

typealias PostsCallback = (List<PostEntity>) -> Unit

class FirebaseModel(private val userViewModel: UserViewModel? = null) {

    private val database = Firebase.firestore

    init {
        val setting = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }

        database.firestoreSettings = setting
    }


    fun getPosts(sinceLastUpdated: Long, callback: PostsCallback) {
        database.collection("posts")
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

    fun saveUserToFirestore(userUid: String?, email: String, username: String, profilePicture: String) {
        if (userUid == null) {
            return
        }

        val userMap = hashMapOf(
            "uid" to userUid,
            "email" to email,
            "username" to username,
            "profilePicture" to profilePicture
        )
        database.collection("users").document(userUid)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("UserViewModel", "User added to Firestore")
            }
            .addOnFailureListener { e ->
                Log.d("UserViewModel", "Error adding user to Firestore", e)
            }
    }

    fun getCurrentUserUsername(callback: (String?) -> Unit) {
        val userId = userViewModel?.getCurrentUserId() ?: return
        database.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    callback(document.getString("username"))
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun getCurrentUserImage(callback: (String?) -> Unit) {
        val userId = userViewModel?.getCurrentUserId() ?: return
        database.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    callback(document.getString("profilePicture"))
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
