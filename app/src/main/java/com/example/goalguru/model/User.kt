package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val profilePicture: String,
    val username: String
)