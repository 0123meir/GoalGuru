package com.example.goalguru.model

import UserViewModel
import com.example.goalguru.model.dao.AppLocalDb
import com.example.goalguru.model.dao.AppLocalDbRepository
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class Model private constructor() {

    private val database: AppLocalDbRepository = AppLocalDb.database
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val mainDispatcher = Dispatchers.Main
    private val firebaseModel = FirebaseModel()
    private val userViewModel = UserViewModel()

    companion object {
        val shared = Model()
    }

    // Get posts with all related data
    fun getPosts(callback: (MutableList<Post>) -> Unit) {
        val lastUpdated: Long = Post.lastUpdated

        firebaseModel.getPosts(lastUpdated) { postsFromDB: List<PostEntity> ->
            coroutineScope.launch {
                var latestTime = lastUpdated
                val posts = mutableListOf<Post>()

                val postJobs = postsFromDB.map { postEntity ->
                    async {
                        database.postDao().insertAll(postEntity)

                        val likesCount = database.likeDao().getLikesCountForPost(postEntity.id)
                        val isLikedByUser = database.likeDao().isPostLikedByUser(postEntity.id, getCurrentUserId())

                        val comments = database.commentDao().getCommentsForPost(postEntity.id).map { commentEntity ->
                            val commenterDeferred = async<Comment> {
                                suspendCancellableCoroutine { continuation ->
                                    firebaseModel.getUserByID(commentEntity.userId) { commenter ->
                                        val comment = Comment(
                                            id = commentEntity.id,
                                            postId = commentEntity.postId,
                                            userId = commentEntity.userId,
                                            text = commentEntity.text,
                                            timestamp = commentEntity.timestamp,
                                            username = commenter?.username ?: "unknown",
                                            userProfilePicture = commenter?.profilePicture ?: ""
                                        )
                                        continuation.resume(comment)
                                    }
                                }
                            }
                            commenterDeferred.await()
                        }

                        val postWithUser = async<Post> {
                            suspendCancellableCoroutine { continuation ->
                                firebaseModel.getUserByID(postEntity.userId) { user ->
                                    val post = Post(
                                        id = postEntity.id,
                                        userId = postEntity.userId,
                                        text = postEntity.text,
                                        imageUrls = postEntity.imageUrls,
                                        likesCount = likesCount,
                                        isLikedByUser = isLikedByUser,
                                        comments = comments.toMutableList(),
                                        timestamp = postEntity.timestamp,
                                        username = user?.username ?: "unknown",
                                        userProfilePicture = user?.profilePicture ?: ""
                                    )
                                    continuation.resume(post)
                                }
                            }
                        }
                        val post = postWithUser.await()

                        postEntity.timestamp.let {
                            if (latestTime < it) {
                                latestTime = it
                            }
                        }

                        post
                    }
                }

                posts.addAll(postJobs.awaitAll())
                Post.lastUpdated = latestTime

                withContext(mainDispatcher) {
                    callback(posts)
                }
            }
        }
    }
    fun updatePost(updatedPost: PostEntity, callback: (Boolean) -> Unit) {
        firebaseModel.updatePost(updatedPost) { success ->
            if (success) {
                coroutineScope.launch {
                    val result = database.postDao().updatePost(updatedPost)
                    withContext(mainDispatcher) {
                        callback(result > 0)
                    }
                }
            } else {
                callback(false)
            }
        }
    }



    // Add a new post
    fun addPost(post: Post, callback: (Boolean) -> Unit) {
        // First add to Firebase
        firebaseModel.addPost(post) { success ->
            if (success) {
                // Then add to local DB
                coroutineScope.launch {
                    val postEntity = PostEntity(
                        id = post.id,
                        userId = post.userId,
                        text = post.text,
                        imageUrls = post.imageUrls,
                        timestamp = post.timestamp ?: System.currentTimeMillis()
                    )

                    database.postDao().insertOne(postEntity)

                    withContext(mainDispatcher) {
                        callback(true)
                    }
                }
            } else {
                callback(false)
            }
        }
    }

    // Delete a post
    fun deletePost(postId: String, callback: (Boolean) -> Unit) {
                coroutineScope.launch {
                    database.postDao().deletePostById(postId)
                    firebaseModel.deletePost(postId)

                    withContext(mainDispatcher) {
                        callback(true)
                    }
                }.invokeOnCompletion { throwable ->
                    if (throwable != null) {
                        coroutineScope.launch {
                            withContext(mainDispatcher) {
                                callback(false)
                            }
                        }
                    }
                }
    }



    // Toggle like on a post
    fun toggleLike(postId: String, callback: (Boolean) -> Unit) {
        coroutineScope.launch {
            val isLiked = database.likeDao().isPostLikedByUser(postId, getCurrentUserId())

            if (isLiked) {
                // Unlike
                database.likeDao().deleteLike(postId, getCurrentUserId())
                firebaseModel.removeLike(postId, getCurrentUserId())
            } else {
                // Like
                val like = LikeEntity(
                    id = UUID.randomUUID().toString(),
                    userId = getCurrentUserId(),
                    postId = postId
                )
                database.likeDao().insertLike(like)
                firebaseModel.addLike(postId, getCurrentUserId())
            }

            withContext(mainDispatcher) {
                callback(true)
            }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                coroutineScope.launch {
                    withContext(mainDispatcher) {
                        callback(false)
                    }
                }
            }
        }
    }

    // Add a comment to a post
    fun addComment(comment: Comment, callback: (Boolean) -> Unit) {
        coroutineScope.launch {


            firebaseModel.addComment(comment) { success ->

                val commentEntity = CommentEntity(
                    id = comment.id,
                    postId = comment.postId,
                    userId = comment.userId,
                    text = comment.text,
                    timestamp = comment.timestamp,
                )

                if (success) {
                    coroutineScope.launch {
                        database.commentDao().insertComment(commentEntity)
                        withContext(mainDispatcher) {
                            callback(true)
                        }
                    }
                } else {
                    callback(false)
                }
            }

        }
    }

    fun getTasks(callback: (List<Task>) -> Unit) {
        coroutineScope.launch {
            val tasks = database.taskDao().getAllTasks(getCurrentUserId())
            withContext(mainDispatcher) {
                callback(tasks)
            }
        }
    }

    fun createTask(task: Task, callback: (Boolean) -> Unit) {
        // First add to Firebase
        firebaseModel.createTask(task) { success ->
            if (success) {
                // Then add to local DB
                coroutineScope.launch {
                    database.taskDao().insertTask(task)
                    withContext(mainDispatcher) {
                        callback(true)
                    }
                }
            } else {
                callback(false)
            }
        }
    }

    fun updateTask(taskId: String, newTask: Task, callback: (Boolean) -> Unit) {
        // First update in Firebase
        firebaseModel.updateTask(taskId, newTask) { success ->
            if (success) {
                // Then update in local DB
                coroutineScope.launch {
                    database.taskDao().updateTask(newTask)
                    withContext(mainDispatcher) {
                        callback(true)
                    }
                }
            } else {
                callback(false)
            }
        }
    }
    fun toggleTaskStatus(taskId: String, callback: (Boolean) -> Unit) {
        coroutineScope.launch {
            // Retrieve the current task status
            val task = database.taskDao().getTaskById(taskId)
            if (task != null) {
                // Toggle the task status
                task.isChecked = !task.isChecked

                // Update the task status in Firebase
                firebaseModel.updateTask(taskId, task) { success ->
                    coroutineScope.launch {
                        if (success) {
                            // Update the task status in the local database
                            database.taskDao().updateTask(task)
                            withContext(mainDispatcher) {
                                callback(true)
                            }
                        } else {
                            withContext(mainDispatcher) {
                                callback(false)
                            }
                        }
                    }
                }
            } else {
                withContext(mainDispatcher) {
                    callback(false)
                }
            }
        }.invokeOnCompletion { throwable ->
            if (throwable != null) {
                coroutineScope.launch {
                    withContext(mainDispatcher) {
                        callback(false)
                    }
                }
            }
        }
    }    fun deleteTask(taskId: String, callback: (Boolean) -> Unit) {
        // First delete from Firebase
        firebaseModel.deleteTask(taskId) { success ->
            if (success) {
                // Then delete from local DB
                coroutineScope.launch {
                    database.taskDao().deleteTaskById(taskId)
                    withContext(mainDispatcher) {
                        callback(true)
                    }
                }
            } else {
                callback(false)
            }
        }
    }

    // Get current user ID
    fun getCurrentUserId(): String {
        return userViewModel.getCurrentUserId() ?: "unknown_user_id"
    }

    // Get current user username
    fun getCurrentUserUsername(): String {
        return userViewModel.username.value ?: "unknown"
    }

    // Get current user profile picture
    fun getCurrentUserImage(): String {
        return userViewModel.profilePicture.value ?: ""
    }

    // Get current user email
    fun getCurrentUserEmail(): String {
        return userViewModel.email.value ?: ""
    }
}