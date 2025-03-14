package com.example.goalguru.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalguru.model.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // This is used to init mock data
    fun insertPosts(posts: List<Post>)

    @Query("SELECT * FROM post")
    fun getAllPosts(): List<Post>

    @Query("SELECT * FROM post WHERE id = :id")
    fun getPostById(id: Int): Post
}