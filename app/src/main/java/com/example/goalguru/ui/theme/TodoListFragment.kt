package com.example.goalguru.ui.theme

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.LoadingViewModel
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
    private lateinit var tasksAdapter: TasksAdapter
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_list, container, false)

        recyclerView = view.findViewById(R.id.task_list)
        newTask = view.findViewById(R.id.new_task)

        tasksAdapter = TasksAdapter(requireContext()) { task, position ->
            openEditTaskBottomSheet(task, position)
        }

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = tasksAdapter

        loadingViewModel.setDataLoaded(false) // Show loading
        loadTasks()

        newTask?.setOnClickListener {
            openNewTaskBottomSheet()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        Model.shared.getTasks { tasks ->
            tasksAdapter.updateTasks(tasks)
            loadingViewModel.setDataLoaded(true) // Hide loading
        }
    }

    private fun openNewTaskBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = NewEditTaskLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        // Set up autocomplete with existing goals
        Model.shared.getTasks { tasks ->
            val existingGoals = tasks.map { it.title }.distinct()
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, existingGoals)
            sheetBinding.taskGoalInput.setAdapter(adapter)
        }

        setupTextWatchers(sheetBinding)

        sheetBinding.saveTaskButton.setOnClickListener {
            if (validateInputs(sheetBinding)) {
                createTask(sheetBinding, dialog)
            }
        }

        dialog.behavior.isFitToContents = true
        dialog.behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }

    private fun openEditTaskBottomSheet(task: Task, position: Int) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = NewEditTaskLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        // Set up autocomplete with existing goals
        Model.shared.getTasks { tasks ->
            val existingGoals = tasks.map { it.title }.distinct()
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, existingGoals)
            sheetBinding.taskGoalInput.setAdapter(adapter)
        }

        // Populate fields with existing task data
        sheetBinding.taskGoalInput.setText(task.title)
        sheetBinding.taskDescriptionInput.setText(task.description)
        sheetBinding.taskDeadlineInput.setText(task.deadline.toString())

        setupTextWatchers(sheetBinding)

        sheetBinding.saveTaskButton.setOnClickListener {
            if (validateInputs(sheetBinding)) {
                updateTask(task, sheetBinding, dialog)
            }
        }

        dialog.behavior.isFitToContents = true
        dialog.behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
    }

    private fun createTask(sheetBinding: NewEditTaskLayoutBinding, dialog: BottomSheetDialog) {
        val newTask = Task(
            userId = Model.shared.getCurrentUserId(),
            title = sheetBinding.taskGoalInput.text.toString(),
            description = sheetBinding.taskDescriptionInput.text.toString(),
            deadline = sheetBinding.taskDeadlineInput.text.toString().toIntOrNull() ?: 0,
            isChecked = false
        )

        Model.shared.createTask(newTask) { success ->
            if (success) {
                loadTasks() // Reload the tasks to update the UI
                dialog.dismiss()
                Toast.makeText(context, "Task created successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to create task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTask(task: Task, sheetBinding: NewEditTaskLayoutBinding, dialog: BottomSheetDialog) {
        val updatedTask = Task(
            id = task.id,
            userId = task.userId,
            title = sheetBinding.taskGoalInput.text.toString(),
            description = sheetBinding.taskDescriptionInput.text.toString(),
            deadline = sheetBinding.taskDeadlineInput.text.toString().toIntOrNull() ?: 0,
            isChecked = task.isChecked
        )

        Model.shared.updateTask(task.id, updatedTask) { success ->
            if (success) {
                loadTasks() // Reload the tasks to update the UI
                dialog.dismiss()
                Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(sheetBinding: NewEditTaskLayoutBinding): Boolean {
        val title = sheetBinding.taskGoalInput.text.toString()
        val description = sheetBinding.taskDescriptionInput.text.toString()
        val deadlineStr = sheetBinding.taskDeadlineInput.text.toString()

        if (title.isBlank()) {
            sheetBinding.taskGoalInput.error = "Title cannot be empty"
            return false
        }

        if (description.isBlank()) {
            sheetBinding.taskDescriptionInput.error = "Description cannot be empty"
            return false
        }

        if (deadlineStr.isBlank()) {
            sheetBinding.taskDeadlineInput.error = "Deadline cannot be empty"
            return false
        }

        try {
            deadlineStr.toInt()
        } catch (e: NumberFormatException) {
            sheetBinding.taskDeadlineInput.error = "Deadline must be a number"
            return false
        }

        return true
    }

    private fun setupTextWatchers(sheetBinding: NewEditTaskLayoutBinding) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sheetBinding.saveTaskButton.isEnabled =
                    sheetBinding.taskGoalInput.text.isNotBlank() &&
                            sheetBinding.taskDescriptionInput.text.isNotBlank() &&
                            sheetBinding.taskDeadlineInput.text.isNotBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        sheetBinding.taskGoalInput.addTextChangedListener(textWatcher)
        sheetBinding.taskDescriptionInput.addTextChangedListener(textWatcher)
        sheetBinding.taskDeadlineInput.addTextChangedListener(textWatcher)
    }
}