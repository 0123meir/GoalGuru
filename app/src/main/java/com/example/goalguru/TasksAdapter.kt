package com.example.goalguru

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.goalguru.model.Task

class TasksAdapter(
    private val context: Context,
    private val onItemClicked: (Task, Int) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<Task>()

    fun getTask(position: Int): Task {
        return tasks[position]
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, position)

        holder.itemView.setOnClickListener {
            onItemClicked(task, position)
        }
    }

    override fun getItemCount() = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val goalTextView: TextView = itemView.findViewById(R.id.goal_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.task_description)
        private val deadlineTextView: TextView = itemView.findViewById(R.id.deadline)
        private val isTaskDone: CheckBox = itemView.findViewById(R.id.is_task_done)
        private val cardView: CardView = itemView.findViewById(R.id.goal_card   )

        fun bind(task: Task, position: Int) {
            goalTextView.text = task.title
            descriptionTextView.text = task.description
            deadlineTextView.text = deadlineTemplate(task.deadline)
            isTaskDone.isChecked = task.isChecked

            cardView.setOnClickListener {
                onItemClicked(task, position)
            }
        }

        private fun deadlineTemplate(days: Int): String {
            return "Deadline: in $days days"
        }
    }
}