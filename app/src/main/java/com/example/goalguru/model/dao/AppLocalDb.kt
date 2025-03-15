package com.example.goalguru.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.goalguru.base.MyApplication
import com.example.goalguru.model.Post
import androidx.room.TypeConverters
import com.example.goalguru.model.Comment

@Database(entities = [Post::class, Comment::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}

object AppLocalDb {
    val database: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.context ?: throw IllegalStateException("AppLocalDb was not initialized")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "app_local_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}