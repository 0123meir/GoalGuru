package com.example.goalguru.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.goalguru.R
import com.example.goalguru.TasksAdapter
import com.example.goalguru.databinding.NewGoalLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoListFragment : Fragment() {
    private var listView: ListView? = null
    private var newTask: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_list, container, false)

        listView = view.findViewById(R.id.task_list)
        newTask = view.findViewById(R.id.new_task)

        listView?.adapter = TasksAdapter()

        newTask?.setOnClickListener {
            openNewTaskBottomSheet()
        }

        return view
    }

    private fun openNewTaskBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = NewGoalLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        dialog.behavior.isFitToContents = true
        dialog.behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED

        dialog.show()
    }
}