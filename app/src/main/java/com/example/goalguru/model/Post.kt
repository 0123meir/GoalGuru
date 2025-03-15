package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Post(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val userProfile: String? = null,
    var text: String,
    val imageUrls: List<String> = emptyList(),
    var likes: Int = 0,
    var likedByUser: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf(),
    val timestamp: Long = System.currentTimeMillis()
)
