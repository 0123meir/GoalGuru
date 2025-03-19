package com.example.goalguru.model

import UserViewModel
import android.util.Log
import com.google.firebase.Firebase
import com.example.goalguru.model.User
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import java.util.UUID

typealias PostsCallback = (List<PostEntity>) -> Unit

class FirebaseModel(private val userViewModel: UserViewModel? = null) {

    private val database = Firebase.firestore

    init {
        val setting = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }

        database.firestoreSettings = setting
    }

    fun updatePost(postId: String, newText: String, callback: (Boolean) -> Unit) {
        database.collection("posts").document(postId)
            .update("text", newText)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseModel", "Error updating post", e)
                callback(false)
            }
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
        database.collection("posts").document(post.id).set(post.json)
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

    fun updateUsername(userUid: String?, username: String) {
        if (userUid == null) {
            return
        }

        val userMap = hashMapOf(
            "username" to username
        ) as Map<String, Any>

        database.collection("users").document(userUid)
            .update(userMap)
            .addOnSuccessListener {
                Log.d("UserViewModel", "Username updated in Firestore")
            }
            .addOnFailureListener { e ->
                Log.d("UserViewModel", "Error updating username in Firestore", e)
            }
    }

    fun updateProfilePic(userUid: String?, profilePicture: String) {
        if (userUid == null) {
            return
        }

        val userMap = hashMapOf(
            "profilePicture" to profilePicture
        ) as Map<String, Any>

        database.collection("users").document(userUid)
            .update(userMap)
            .addOnSuccessListener {
                Log.d("UserViewModel", "Profile picture updated in Firestore")
                userViewModel?.updateUserData()
            }
            .addOnFailureListener { e ->
                Log.d("UserViewModel", "Error updating profile picture in Firestore", e)
            }
    }

    fun addComment(comment: Comment, callback: (Boolean) -> Unit) {
        val commentMap = hashMapOf(
            "id" to comment.id,
            "postId" to comment.postId,
            "userId" to comment.userId,
            "text" to comment.text,
            "timestamp" to comment.timestamp,
            "username" to comment.username,
            "userProfilePicture" to comment.userProfilePicture
        )

        database.collection("comments").document(comment.id).set(commentMap)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }

    fun createTask(task: Task, callback: (Boolean) -> Unit) {
        val taskMap = hashMapOf(
            "id" to task.id,
            "userId" to task.userId,
            "title" to task.title,
            "description" to task.description,
            "deadline" to task.deadline,
            "isChecked" to task.isChecked
        )

        database.collection("tasks").document(task.id).set(taskMap)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }

    fun updateTask(taskId: String, newTask: Task, callback: (Boolean) -> Unit) {
        val taskMap = hashMapOf(
            "id" to newTask.id,
            "userId" to newTask.userId,
            "title" to newTask.title,
            "description" to newTask.description,
            "deadline" to newTask.deadline,
            "isChecked" to newTask.isChecked
        )

        database.collection("tasks").document(taskId).set(taskMap)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }

    // Add this function to FirebaseModel.kt
    fun deleteTask(taskId: String, callback: (Boolean) -> Unit) {
        database.collection("tasks").document(taskId).delete()
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }

    fun getUserByID(userId: String, callback: (User?) -> Unit) {
        database.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = User(
                        id = document.getString("uid") ?: "",
                        email = document.getString("email") ?: "",
                        username = document.getString("username") ?: "",
                        profilePicture = document.getString("profilePicture") ?: ""
                    )
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun addLike(postId: String, userId: String) {
        val like = LikeEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            postId = postId
        )

        database.collection("likes").document(like.id).set(like.json)
    }

    fun removeLike(postId: String, userId: String) {
        database.collection("likes")
            .whereEqualTo(LikeEntity.KEY_POST_ID, postId)
            .whereEqualTo(LikeEntity.KEY_USER_ID, userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    database.collection("likes").document(document.id).delete()
                }
            }
    }
}
