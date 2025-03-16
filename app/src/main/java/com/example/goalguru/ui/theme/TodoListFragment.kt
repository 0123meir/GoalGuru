package com.example.goalguru.ui.theme

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.R
import com.example.goalguru.TasksAdapter
import com.example.goalguru.databinding.NewEditTaskLayoutBinding
import com.example.goalguru.model.Model
import com.example.goalguru.model.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodoListFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var newTask: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_list, container, false)

        recyclerView = view.findViewById(R.id.task_list)
        newTask = view.findViewById(R.id.new_task)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = TasksAdapter(requireContext()) { task, position ->
            openNewTaskBottomSheet(task, position)
        }

        newTask?.setOnClickListener {
            openNewTaskBottomSheet(null, -1)
        }

        return view
    }

    private fun openNewTaskBottomSheet(task: Task?, position: Int) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = NewEditTaskLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        val existingGoals = Model.shared.tasks.map { it.goal }.distinct()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, existingGoals)
        sheetBinding.taskGoalInput.setAdapter(adapter)

        task?.let {
            sheetBinding.taskGoalInput.setText(it.goal)
            sheetBinding.taskDescriptionInput.setText(it.description)
            sheetBinding.taskDeadlineInput.setText(it.deadline.toInt().toString()) // pick number from deadline
        }

        sheetBinding.saveTaskButton.setOnClickListener {
            saveTask(sheetBinding, task, position, dialog)
        }

        val textWatcher = createTextWatcher(sheetBinding)

        sheetBinding.taskGoalInput.addTextChangedListener(textWatcher)
        sheetBinding.taskDescriptionInput.addTextChangedListener(textWatcher)
        sheetBinding.taskDeadlineInput.addTextChangedListener(textWatcher)

        sheetBinding.saveTaskButton.isEnabled = sheetBinding.taskGoalInput.text.isNotBlank() &&
                sheetBinding.taskDescriptionInput.text.isNotBlank() &&
                sheetBinding.taskDeadlineInput.text.isNotBlank()

        dialog.behavior.isFitToContents = true
        dialog.behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }

    private fun saveTask(sheetBinding: NewEditTaskLayoutBinding, task: Task?, position: Int, dialog: BottomSheetDialog) {
        val newTask = Task(
            goal = sheetBinding.taskGoalInput.text.toString(),
            description = sheetBinding.taskDescriptionInput.text.toString(),
            deadline = sheetBinding.taskDeadlineInput.text.toString().toInt(),
            isChecked = task?.isChecked ?: false
        )

        if (position >= 0) {
            Model.shared.tasks[position] = newTask
        } else {
            Model.shared.tasks.add(newTask)
        }

        (recyclerView?.adapter as? TasksAdapter)?.notifyDataSetChanged()
        dialog.dismiss()
    }

    private fun createTextWatcher(sheetBinding: NewEditTaskLayoutBinding): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sheetBinding.saveTaskButton.isEnabled = sheetBinding.taskGoalInput.text.isNotBlank() &&
                        sheetBinding.taskDescriptionInput.text.isNotBlank() &&
                        sheetBinding.taskDeadlineInput.text.isNotBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }
}