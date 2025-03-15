package com.example.goalguru

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.goalguru.databinding.NewGoalLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class TodoListActivity : AppCompatActivity() {
    private var listView: ListView? = null
    private var newTask: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list)

        listView = findViewById(R.id.task_list)
        newTask = findViewById(R.id.new_task)

        listView?.adapter = TasksAdapter()

        newTask?.setOnClickListener() {
            openNewTaskBottomSheet()
        }
    }

    private fun openNewTaskBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val sheetBinding = NewGoalLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        dialog.behavior.isFitToContents = true
        dialog.behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED

        dialog.show()
    }
}