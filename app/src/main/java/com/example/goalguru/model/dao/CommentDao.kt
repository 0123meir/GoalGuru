package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalguru.model.CommentEntity

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId")
    suspend fun getCommentsForPost(postId: String): List<CommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}