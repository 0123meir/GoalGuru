package com.example.goalguru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.goalguru.models.Model
import com.example.goalguru.models.Task

class TasksAdapter() : BaseAdapter() {
    private var tasks: MutableList<Task>? = Model.shared.tasks // TODO: Replace mock with data

    override fun getCount(): Int = tasks?.size ?: 0

    override fun getItem(position: Int): Any {
        return 1;
    }

    override fun getItemId(position: Int): Long {
        return 1;
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent?.context)
        val view = convertView ?: inflater.inflate(R.layout.task_row, parent, false)

        val task = tasks?.get(position)

        val goalTextView: TextView = view.findViewById(R.id.goal_name)
        val descriptionTextView: TextView = view.findViewById(R.id.task_description)
        val deadlineTextView: TextView = view.findViewById(R.id.deadline)
        val isTaskDone: CheckBox = view.findViewById(R.id.is_task_done)

        goalTextView.text = task?.goal
        descriptionTextView.text = task?.description
        deadlineTextView.text = task?.deadline
        isTaskDone.isChecked = task?.isChecked ?: false

        return view
    }
}
