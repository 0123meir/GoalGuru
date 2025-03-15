package com.example.goalguru.ui.theme

import java.util.UUID

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)