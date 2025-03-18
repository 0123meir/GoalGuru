package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goalguru.model.LikeEntity

@Dao
interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLike(like: LikeEntity)

    @Update
    fun updateLike(like: LikeEntity)

    @Query("SELECT * FROM likes WHERE id = :likeId")
    fun getLikeById(likeId: String): LikeEntity?

    @Query("DELETE FROM likes WHERE id = :likeId")
    fun deleteLikeById(likeId: String)
}