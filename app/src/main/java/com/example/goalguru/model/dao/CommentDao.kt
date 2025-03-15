package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalguru.model.Comment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(comments: List<Comment>)

    @Query("SELECT * FROM comment")
    fun getAllComments(): List<Comment>

    @Query("SELECT * FROM comment WHERE id = :id")
    fun getCommentById(id: Int): Comment
}