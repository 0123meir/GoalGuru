package com.example.goalguru.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.goalguru.base.MyApplication
import androidx.room.TypeConverters
import com.example.goalguru.model.CommentEntity
import com.example.goalguru.model.PostEntity

@Database(entities = [PostEntity::class, CommentEntity::class], version = 8)
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