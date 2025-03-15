package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Comment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)