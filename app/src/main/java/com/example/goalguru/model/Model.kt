package com.example.goalguru.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.goalguru.model.dao.AppLocalDb
import com.example.goalguru.model.dao.AppLocalDbRepository
import com.example.goalguru.model.Task
import com.example.goalguru.util.MockDataProvider
import java.util.concurrent.Executors

class Model private constructor() {

    private val database: AppLocalDbRepository = AppLocalDb.database
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    val tasks: MutableList<Task> = ArrayList()

    companion object {
        val shared = Model()
    }

    init {
        insertMockData{}

        // TODO: Liraz - insert mock data into the database
        tasks.add(
            Task(
                "Save for Vacation",
                "Set up a dedicated savings account - I'm making this one extra long to see how it handles text wrapping",
                5,
                false)
        )
        tasks.add(Task("Save for Vacation", "Cut dining out expenses by 50%", 7, false))
        tasks.add(Task("Save for Vacation", "Research budget travel options", 10, false))
        tasks.add(Task("Save for Vacation", "Save $200 from each paycheck", 15, true))
        tasks.add(Task("Run a 5K", "Buy running shoes", 3, true))
        tasks.add(Task("Run a 5K", "Create a training schedule", 5, false))
        tasks.add(Task("Run a 5K", "Run 1K without stopping", 10, false))
        tasks.add(Task("Run a 5K", "Increase distance to 3K", 20, false))
        tasks.add(Task("Run a 5K", "Sign up for a local 5K race", 25, false))
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