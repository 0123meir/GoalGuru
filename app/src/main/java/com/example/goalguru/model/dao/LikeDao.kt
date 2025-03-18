package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goalguru.model.LikeEntity

@Dao
interface LikeDao {
    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    suspend fun getLikesCountForPost(postId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE postId = :postId AND userId = :userId LIMIT 1)")
    suspend fun isPostLikedByUser(postId: String, userId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikeEntity)

    @Query("DELETE FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun deleteLike(postId: String, userId: String)
}