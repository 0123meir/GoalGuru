package com.example.goalguru.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.goalguru.base.MyApplication
import com.example.goalguru.model.Post

@Database(entities = [Post::class], version = 1)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
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