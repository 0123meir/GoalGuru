// Entity Classes (For Room Database)

import androidx.room.*
import java.util.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: UUID,
    val username: String,
    val password: String,
    val profilePicture: String
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "post_images")
data class PostImageEntity(
    @PrimaryKey val id: UUID,
    val postId: UUID,
    val imageUrl: String
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: UUID,
    val postId: UUID,
    val userId: UUID,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val postId: UUID,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val title: String,
    val deadline: Long
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val goalId: UUID,
    val title: String,
    val deadline: Long,
    val isChecked: Boolean
)

// Relationship Classes

// For fetching a post with its image URLs
data class PostWithImages(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val images: List<PostImageEntity>
)

// For fetching a post with its comments and user information
data class PostWithComments(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val comments: List<CommentEntity>,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity
)

// For fetching a goal with its tasks
data class GoalWithTasks(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val tasks: List<TaskEntity>
)

// Model Classes (For UI representation)

data class User(
    val id: UUID,
    val username: String,
    val profilePicture: String
)

data class Post(
    val id: UUID,
    val userId: UUID,
    val text: String,
    val imageUrls: List<String>,
    val timestamp: Long,
    val likesCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val username: String = "",
    val userProfilePicture: String = ""
)

data class Comment(
    val id: UUID,
    val userId: UUID,
    val postId: UUID,
    val text: String,
    val timestamp: Long,
    val username: String = "",
    val userProfilePicture: String = ""
)

data class Goal(
    val id: UUID,
    val userId: UUID,
    val title: String,
    val deadline: Long,
    val tasks: List<Task> = emptyList(),
    val completedTasksCount: Int = 0
)

data class Task(
    val id: UUID,
    val userId: UUID,
    val goalId: UUID,
    val title: String,
    val deadline: Long,
    val isChecked: Boolean
)

// Mapper Classes

object UserMapper {
    fun toUser(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            profilePicture = entity.profilePicture
        )
    }
}

object PostMapper {
    fun toPost(
        postWithImages: PostWithImages,
        likesCount: Int,
        isLikedByUser: Boolean,
        comments: List<Comment>,
        user: User
    ): Post {
        return Post(
            id = postWithImages.post.id,
            userId = postWithImages.post.userId,
            text = postWithImages.post.text,
            imageUrls = postWithImages.images.map { it.imageUrl },
            timestamp = postWithImages.post.timestamp,
            likesCount = likesCount,
            isLikedByUser = isLikedByUser,
            comments = comments,
            username = user.username,
            userProfilePicture = user.profilePicture
        )
    }
}

object CommentMapper {
    fun toComment(entity: CommentEntity, username: String, userProfilePicture: String): Comment {
        return Comment(
            id = entity.id,
            userId = entity.userId,
            postId = entity.postId,
            text = entity.text,
            timestamp = entity.timestamp,
            username = username,
            userProfilePicture = userProfilePicture
        )
    }
}

object GoalMapper {
    fun toGoal(goalWithTasks: GoalWithTasks): Goal {
        val tasks = goalWithTasks.tasks.map { taskEntity ->
            Task(
                id = taskEntity.id,
                userId = taskEntity.userId,
                goalId = taskEntity.goalId,
                title = taskEntity.title,
                deadline = taskEntity.deadline,
                isChecked = taskEntity.isChecked
            )
        }
        
        return Goal(
            id = goalWithTasks.goal.id,
            userId = goalWithTasks.goal.userId,
            title = goalWithTasks.goal.title,
            deadline = goalWithTasks.goal.deadline,
            tasks = tasks,
            completedTasksCount = tasks.count { it.isChecked }
        )
    }
}
