package com.example.goalguru.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.goalguru.base.MyApplication
import androidx.room.TypeConverters
import com.example.goalguru.model.CommentEntity
import com.example.goalguru.model.LikeEntity
import com.example.goalguru.model.PostEntity
import com.example.goalguru.model.PostImageEntity
import com.example.goalguru.model.Task

@Database(entities = [PostEntity::class, CommentEntity::class,
                        Task::class, PostImageEntity::class, LikeEntity::class], version = 10)
@TypeConverters(Converters::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun userDao(): UserDao
    abstract fun likeDao(): LikeDao
    abstract fun taskDao(): TaskDao

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