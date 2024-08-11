package com.valenpatel.paisefy.adapter

import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.databinding.TodoListBinding
import com.valenpatel.paisefy.db.entities.TodoEntity
import java.text.SimpleDateFormat
import java.util.Locale

class TodoAdapter(val context: Context, var todoList: List<TodoEntity>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    fun updateTodos(newTodos: List<TodoEntity>) {
        todoList = newTodos
        notifyDataSetChanged()
    }
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TodoListBinding = TodoListBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {

        return TodoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.todo_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.binding.note.text = todo.title
        holder.binding.dateTxt.text = todo.date
        holder.binding.timeTxt.text = todo.time

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = simpleDateFormat.format(calendar.time)
        if(todo.date <= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)) {
            if (todo.time < time) {
                holder.binding.imageView2.setImageResource(R.drawable.baseline_notifications_off_24)
                //     holder.binding.imageView2.visibility = View.INVISIBLE
                holder.binding.dateTxt.setTextColor(Color.RED)
                holder.binding.timeTxt.setTextColor(Color.RED)
            }
        }
    }
}