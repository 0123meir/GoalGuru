package com.example.goalguru.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import java.util.UUID

typealias PostsCallback = (List<PostEntity>) -> Unit

class FirebaseModel {

    private val database = Firebase.firestore

    init {
        val setting = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
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
        database.collection("posts").document(post.id).set(post.json)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
    }

    fun deletePost(postId: String, callback: (Boolean) -> Unit) {
        database.collection("posts").document(postId).delete()
            .addOnCompleteListener {
                callback(it.isSuccessful)
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
}

    //todo: Liraz - delete if not needed when creating profile page
//    fun addUser(user: User, callback: (Boolean) -> Unit) {
//        val userMap = hashMapOf(
//            "id" to user.id,
//            "username" to user.username,
//            "profilePicture" to user.profilePicture
//        )
//
//        database.collection("users").document(user.id).set(userMap)
//            .addOnCompleteListener {
//                callback(it.isSuccessful)
//            }
//    }

//    fun getUser(userId: String, callback: (User?) -> Unit) {
//        database.collection("users").document(userId).get()
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    val user = it.result.toObject(User::class.java)
//                    callback(user)
//                } else {
//                    callback(null)
//                }
//            }
//    }
//
//    fun updateUser(userId: String, newUsername: String, newProfilePicture: String, callback: (Boolean) -> Unit) {
//        val userMap = hashMapOf(
//            "username" to newUsername,
//            "profilePicture" to newProfilePicture
//        )
//
//        database.collection("users").document(userId).update(userMap as Map<String, Any>)
//            .addOnCompleteListener {
//                callback(it.isSuccessful)
//            }
//    }
//
//    fun deleteUser(userId: String, callback: (Boolean) -> Unit) {
//        database.collection("users").document(userId).delete()
//            .addOnCompleteListener {
//                callback(it.isSuccessful)
//            }
//    }
//}