package com.example.goalguru.model


// Entity Classes (For Room Database)

import androidx.room.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val password: String,
    val profilePicture: String
)

data class User(
    val id: String,
    val username: String,
    val profilePicture: String
) {

    companion object {
        const val KEY_ID = "id"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        const val KEY_PROFILE_PICTURE = "profilePicture"

        fun fromJSON(json: Map<String, Any>): UserEntity {
            val id = json[KEY_ID] as String
            val username = json[KEY_USERNAME] as String
            val password = json[KEY_PASSWORD] as String
            val profilePicture = json[KEY_PROFILE_PICTURE] as String

            return UserEntity(
                id = id,
                username = username,
                password = password,
                profilePicture = profilePicture
            )
        }
    }

    val json: HashMap<String, Any?>
        get() = hashMapOf(
            KEY_ID to id,
            KEY_USERNAME to username,
            KEY_PROFILE_PICTURE to profilePicture
        )
}

@Entity(tableName = "post_images")
data class PostImageEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val imageUrl: String
) {

    companion object {
        const val KEY_ID = "id"
        const val KEY_POST_ID = "postId"
        const val KEY_IMAGE_URL = "imageUrl"

        fun fromJSON(json: Map<String, Any>): PostImageEntity {
            val id = json[KEY_ID] as String
            val postId = json[KEY_POST_ID] as String
            val imageUrl = json[KEY_IMAGE_URL] as String

            return PostImageEntity(
                id = id,
                postId = postId,
                imageUrl = imageUrl
            )
        }
    }

    val json: HashMap<String, Any?>
        get() = hashMapOf(
            KEY_ID to id,
            KEY_POST_ID to postId,
            KEY_IMAGE_URL to imageUrl
        )
}

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val postId: String,
    val timestamp: Long = System.currentTimeMillis()
) {

    companion object {
        const val KEY_ID = "id"
        const val KEY_USER_ID = "userId"
        const val KEY_POST_ID = "postId"
        const val KEY_TIMESTAMP = "timestamp"

        fun fromJSON(json: Map<String, Any>): LikeEntity {
            val id = json[KEY_ID] as String
            val userId = json[KEY_USER_ID] as String
            val postId = json[KEY_POST_ID] as String
            val timestamp = json[KEY_TIMESTAMP] as Long

            return LikeEntity(
                id = id,
                userId = userId,
                postId = postId,
                timestamp = timestamp
            )
        }
    }

    val json: HashMap<String, Any?>
        get() = hashMapOf(
            KEY_ID to id,
            KEY_USER_ID to userId,
            KEY_POST_ID to postId,
            KEY_TIMESTAMP to timestamp
        )
}

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
    val user: User
)

// Mapper Classes

object UserMapper {
    fun toUser(entity: User): User {
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
