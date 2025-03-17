package com.example.goalguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val deadline: Long
)

data class Goal(
    val id: String,
    val userId: String,
    val title: String,
    val deadline: Long,
    val tasks: List<Task> = emptyList(),
    val completedTasksCount: Int = 0
)