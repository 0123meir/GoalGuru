package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.goalguru.model.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(post: PostEntity)

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): MutableList<PostEntity>

    // Add these new functions
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    // Get posts with related data
    @Transaction
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    suspend fun getPostsWithRelatedData(): List<PostEntity>

    @Query("UPDATE posts SET text = :text WHERE id = :postId")
    suspend fun updatePostText(postId: String, text: String): Int
}

