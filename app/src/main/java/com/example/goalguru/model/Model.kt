package com.example.goalguru.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.goalguru.model.dao.AppLocalDb
import com.example.goalguru.model.dao.AppLocalDbRepository
import com.example.goalguru.util.MockDataProvider
import java.util.concurrent.Executors

class Model private constructor() {

    private val database: AppLocalDbRepository = AppLocalDb.database
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = Model()
    }

    init {
        insertMockData{}
    }

    private fun insertMockData(callback: () -> Unit) {
        executor.execute {
            database.postDao().insertPosts(initPosts())
            mainHandler.post {
                callback()
            }
        }
    }

    private fun initPosts(): MutableList<Post> {
        return MockDataProvider.generateMockPosts(7)
    }

    fun getPosts(callback: (MutableList<Post>) -> Unit) {
        executor.execute {
            val posts = database.postDao().getAllPosts()
            mainHandler.post {
                callback(posts)
            }
        }
    }
}