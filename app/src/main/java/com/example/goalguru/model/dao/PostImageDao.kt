package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goalguru.model.PostImageEntity

@Dao
interface PostImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostImage(postImage: PostImageEntity)

    @Update
    fun updatePostImage(postImage: PostImageEntity)

    @Query("SELECT * FROM post_images WHERE id = :postImageId")
    fun getPostImageById(postImageId: String): PostImageEntity?

    @Query("DELETE FROM post_images WHERE id = :postImageId")
    fun deletePostImageById(postImageId: String)
}