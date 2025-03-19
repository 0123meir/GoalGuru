import com.example.goalguru.model.Comment
import com.example.goalguru.model.CommentEntity
import com.example.goalguru.model.FirebaseModel
import com.example.goalguru.model.LikeEntity
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity
import com.example.goalguru.model.Task
import com.example.goalguru.model.User
import com.example.goalguru.model.dao.AppLocalDb
import com.example.goalguru.model.dao.AppLocalDbRepository
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Model private constructor() {

    private val database: AppLocalDbRepository = AppLocalDb.database
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val mainDispatcher = Dispatchers.Main
    private val firebaseModel = FirebaseModel()
    private val currentUserId: String = "user_id_placeholder" // Replace with actual user ID from auth

    companion object {
        val shared = Model()
    }

    // Get posts with all related data
    fun getPosts(callback: (MutableList<Post>) -> Unit) {
        val lastUpdated: Long = Post.lastUpdated

        // Fetch from Firebase first
        firebaseModel.getPosts(lastUpdated) { list: List<PostEntity> ->
            coroutineScope.launch {
                var latestTime = lastUpdated

                // Insert fetched posts into local DB
                for (post in list) {
                    database.postDao().insertAll(post)

                    post.timestamp.let {
                        if (latestTime < it) {
                            latestTime = it
                        }
                    }
                }

                Post.lastUpdated = latestTime

                // Now fetch complete posts with related data
                val posts = getCompletePostsFromLocalDb()

                withContext(mainDispatcher) {
                    callback(posts)
                }
            }
        }
    }

    fun updatePost(postId: String, newText: String, callback: (Boolean) -> Unit) {
        // First update in Firebase
        firebaseModel.updatePost(postId, newText) { success ->
            if (success) {
                // Then update in local DB
                coroutineScope.launch {
                    val result = database.postDao().updatePostText(postId, newText)
                    withContext(mainDispatcher) {
                        callback(result > 0)
                    }
                }
            } else {
                callback(false)
            }
        }
    }

    // Helper function to get complete posts from local DB
    private suspend fun getCompletePostsFromLocalDb(): MutableList<Post> {
        val posts = mutableListOf<Post>()
        val postEntities = database.postDao().getAllPosts()

        for (postEntity in postEntities) {
            // Get user data
            val userEntity = database.userDao().getUserById(postEntity.userId)
            val user = userEntity?.let {
                User(
                    id = it.id,
                    username = it.username,
                    profilePicture = it.profilePicture
                )
            } ?: User(id = "", username = "Unknown", profilePicture = "")

            // Get comments
            val commentEntities = database.commentDao().getCommentsForPost(postEntity.id)
            val comments = commentEntities.map { commentEntity ->
                val commentUser = database.userDao().getUserById(commentEntity.userId)
                Comment(
                    id = commentEntity.id,
                    postId = commentEntity.postId,
                    userId = commentEntity.userId,
                    text = commentEntity.text,
                    timestamp = commentEntity.timestamp,
                    username = commentUser?.username ?: "Unknown",
                    userProfilePicture = commentUser?.profilePicture ?: ""
                )
            }.toMutableList()

            // Get likes
            val likesCount = database.likeDao().getLikesCountForPost(postEntity.id)
            val isLikedByUser = database.likeDao().isPostLikedByUser(postEntity.id, currentUserId)

            // Create complete post
            val post = Post(
                id = postEntity.id,
                userId = postEntity.userId,
                text = postEntity.text,
                imageUrls = postEntity.imageUrls,
                timestamp = postEntity.timestamp,
                likesCount = likesCount,
                isLikedByUser = isLikedByUser,
                comments = comments,
                username = user.username,
                userProfilePicture = user.profilePicture
            )

            posts.add(post)
        }

        return posts
    }

    // Get a single post by ID
    fun getPostById(postId: String, callback: (Post?) -> Unit) {
        coroutineScope.launch {
            val postEntity = database.postDao().getPostById(postId)

            if (postEntity != null) {
                val userEntity = database.userDao().getUserById(postEntity.userId)
                val user = userEntity?.let {
                    User(
                        id = it.id,
                        username = it.username,
                        profilePicture = it.profilePicture
                    )
                } ?: User(id = "", username = "Unknown", profilePicture = "")

                val commentEntities = database.commentDao().getCommentsForPost(postEntity.id)
                val comments = commentEntities.map { commentEntity ->
                    val commentUser = database.userDao().getUserById(commentEntity.userId)
                    Comment(
                        id = commentEntity.id,
                        postId = commentEntity.postId,
                        userId = commentEntity.userId,
                        text = commentEntity.text,
                        timestamp = commentEntity.timestamp,
                        username = commentUser?.username ?: "Unknown",
                        userProfilePicture = commentUser?.profilePicture ?: ""
                    )
                }.toMutableList()

                val likesCount = database.likeDao().getLikesCountForPost(postEntity.id)
                val isLikedByUser = database.likeDao().isPostLikedByUser(postEntity.id, currentUserId)

                val post = Post(
                    id = postEntity.id,
                    userId = postEntity.userId,
                    text = postEntity.text,
                    imageUrls = postEntity.imageUrls,
                    timestamp = postEntity.timestamp,
                    likesCount = likesCount,
                    isLikedByUser = isLikedByUser,
                    comments = comments,
                    username = user.username,
                    userProfilePicture = user.profilePicture
                )

                withContext(mainDispatcher) {
                    callback(post)
                }
            } else {
                withContext(mainDispatcher) {
                    callback(null)
                }
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
        // First delete from Firebase
        firebaseModel.deletePost(postId) { success ->
            if (success) {
                // Then delete from local DB
                coroutineScope.launch {
                    database.postDao().deletePostById(postId)
                    withContext(mainDispatcher) {
                        callback(true)
                    }
                }
            } else {
                callback(false)
            }
        }
    }

    // Toggle like on a post
    fun toggleLike(postId: String, callback: (Boolean) -> Unit) {
        coroutineScope.launch {
            val isLiked = database.likeDao().isPostLikedByUser(postId, currentUserId)

            if (isLiked) {
                // Unlike
                database.likeDao().deleteLike(postId, currentUserId)
                firebaseModel.removeLike(postId, currentUserId)
            } else {
                // Like
                val like = LikeEntity(
                    id = UUID.randomUUID().toString(),
                    userId = currentUserId,
                    postId = postId
                )
                database.likeDao().insertLike(like)
                firebaseModel.addLike(postId, currentUserId)
            }

            val newIsLiked = !isLiked

            withContext(mainDispatcher) {
                callback(newIsLiked)
            }
        }
    }

    // Add a comment to a post
    fun addComment(comment: Comment, callback: (Boolean) -> Unit) {
        coroutineScope.launch {
            val commentEntity = CommentEntity(
                id = comment.id,
                postId = comment.postId,
                userId = comment.userId,
                text = comment.text,
                timestamp = comment.timestamp,
            )

            database.commentDao().insertComment(commentEntity)

            // Also add to Firebase
            withContext(mainDispatcher) {
                firebaseModel.addComment(comment) { success ->
                    callback(success)
                }
            }
        }
    }

    fun getTasks(callback: (List<Task>) -> Unit) {
        coroutineScope.launch {
            val tasks = database.taskDao().getAllTasks(currentUserId)
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

    fun deleteTask(taskId: String, callback: (Boolean) -> Unit) {
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
}