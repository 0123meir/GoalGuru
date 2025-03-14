package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey val id: Int,
    val userName: String,
    val text: String,
//    val images: List<String>,
    var likes: Int
)
