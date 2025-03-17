package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalguru.model.Post
import com.example.goalguru.model.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // This is used to init mock data
    fun insertPosts(posts: MutableList<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(post: PostEntity)

    @Query("SELECT * FROM posts")
    fun getAllPosts(): MutableList<PostEntity>
}