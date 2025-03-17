package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String = "",
    val description: String = "",
    val deadline: Int = 0,
    val isChecked: Boolean = false
) {
}