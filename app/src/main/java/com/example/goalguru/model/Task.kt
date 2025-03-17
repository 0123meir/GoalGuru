package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Task(
    val id: String,
    val userId: String,
    val goalId: String,
    val title: String,
    val description: String,
    val deadline: Int,
    val isChecked: Boolean
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val goalId: String,
    val title: String,
    val description: String,
    val deadline: Int,
    val isChecked: Boolean
) {
}