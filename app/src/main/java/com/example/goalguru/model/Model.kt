package com.example.goalguru.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.goalguru.model.dao.AppLocalDb
import com.example.goalguru.model.dao.AppLocalDbRepository
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
            database.postDao().insertPosts(initYourPosts())
            mainHandler.post {
                callback()
            }
        }
    }

    private fun initYourPosts(): List<Post> {
        return listOf(
            Post(1,"Meir Cohen", "Hello everyone!",5),
            Post(2, "Meir Cohen", "Check out this cool picture!",  10)
        )
    }

    private fun initFriendsPosts(): List<Post> {
        return listOf(
            Post(3, "John Doe", "Hello everyone!", 5),
            Post(4, "Liraz Cohen", "Check out this cool picture!", 10)
        )
    }

    fun getPosts(callback: (List<Post>) -> Unit) {
        executor.execute {
            val posts = database.postDao().getAllPosts()
            mainHandler.post {
                callback(posts)
            }
        }
    }
}