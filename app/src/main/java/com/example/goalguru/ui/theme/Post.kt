package com.example.goalguru.ui.theme

import java.util.UUID

data class Post(
    val id: String = UUID.randomUUID().toString(),
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