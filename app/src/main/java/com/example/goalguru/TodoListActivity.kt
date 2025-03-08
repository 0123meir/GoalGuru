package com.example.goalguru

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class TodoListActivity : AppCompatActivity() {
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list)

        listView = findViewById(R.id.task_list)

        listView?.adapter = TasksAdapter()
    }
}