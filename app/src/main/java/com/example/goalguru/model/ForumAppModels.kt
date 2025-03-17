package com.example.goalguru.model


// Entity Classes (For Room Database)

import androidx.room.*
import java.util.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val password: String,
    val profilePicture: String
)



@Entity(tableName = "post_images")
data class PostImageEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val imageUrl: String
)

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val postId: String,
    val timestamp: Long = System.currentTimeMillis()
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


// Model Classes (For UI representation)

data class User(
    val id: String,
    val username: String,
    val profilePicture: String
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
        comments: MutableList<Comment>,
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
