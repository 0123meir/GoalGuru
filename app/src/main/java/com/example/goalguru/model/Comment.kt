package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    constructor() : this("", "", "", "", 0L)
}

data class Comment(
    val id: String,
    val userId: String,
    val postId: String,
    val text: String,
    val timestamp: Long,
    val username: String = "",
    val userProfilePicture: String = ""
)

