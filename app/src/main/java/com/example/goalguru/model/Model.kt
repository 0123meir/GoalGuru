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

    private val firebaseModel = FirebaseModel()

    val tasks: MutableList<Task> = ArrayList()

    companion object {
        val shared = Model()
    }

    fun getPosts(callback: (MutableList<PostEntity>) -> Unit) {
        val lastUpdated: Long = Post.lastUpdated

        firebaseModel.getPosts(lastUpdated) { list: List<PostEntity> ->
            executor.execute {
                var latestTime = lastUpdated

                for (post in list) {
                    database.postDao().insertAll(post)

                    post.timestamp.let {
                        if (latestTime < it) {
                            latestTime = it
                        }
                    }
                }

                Post.lastUpdated = lastUpdated
                val posts = database.postDao().getAllPosts()

                mainHandler.post {
                    callback(posts)
                }
            }

        }
    }
}