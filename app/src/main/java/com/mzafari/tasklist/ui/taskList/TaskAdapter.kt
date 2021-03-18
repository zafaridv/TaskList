package com.mzafari.tasklist.ui.taskList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.mzafari.tasklist.R
import com.mzafari.tasklist.model.Task
import com.mzafari.tasklist.model.TaskStatus

/**
 * Task list adapter with diffutil
 */
class TaskAdapter(private val context: Context,private val listener: TaskListListener) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(
    TasksComparator()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current,context,listener)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.card_view)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val deadline: TextView = itemView.findViewById(R.id.deadline)
        private val status: ImageView = itemView.findViewById(R.id.status)
        private val edit: ImageButton = itemView.findViewById(R.id.edit)
        private val delete: ImageButton = itemView.findViewById(R.id.delete)

        fun bind(task: Task,context:Context,listener: TaskListListener) {
            //set transition name for animation
            //card.transitionName = "taskItem${task.id}"

            name.text = task.name
            deadline.text = task.deadline
            if(task.status.equals(TaskStatus.done)){
                status.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_done_24))
            }else{
                status.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_onprogress_24))
            }
            edit.setOnClickListener{
                listener.onTaskEditClicked(card,task)
            }
            delete.setOnClickListener{
                listener.onTaskDeleteClicked(task)
            }

        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_list_item, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    /**
     * Compare the submitted list with old list and animate the changed item properly
     */
    class TasksComparator : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return (oldItem.name == newItem.name
                    && oldItem.deadline == newItem.deadline
                    && oldItem.status == newItem.status
                    )
        }
    }
}