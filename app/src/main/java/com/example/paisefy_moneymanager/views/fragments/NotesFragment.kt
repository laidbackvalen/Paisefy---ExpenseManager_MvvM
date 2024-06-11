package com.example.paisefy_moneymanager.views.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.adapter.TodoAdapter
import com.example.paisefy_moneymanager.databinding.FragmentNotesBinding
import com.example.paisefy_moneymanager.databinding.TodoLayoutBinding
import com.example.paisefy_moneymanager.db.entities.TodoEntity
import com.example.paisefy_moneymanager.receiver.alarm.AlarmReceiver
import com.example.paisefy_moneymanager.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    private val viewModel: MainViewModel by activityViewModels()
   lateinit var todoAdapter : TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        binding.floatingActionButtonNote.setOnClickListener {
            val todoBinding: TodoLayoutBinding = TodoLayoutBinding.inflate(layoutInflater)
            val alertDialog = AlertDialog.Builder(requireContext()).create()
            alertDialog.setView(todoBinding.root)
            val calendar = Calendar.getInstance()

            //date
            todoBinding.date.setOnClickListener {
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(requireContext(), { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val date = simpleDateFormat.format(calendar.time)
                    todoBinding.date.setText(date)
                }, year, month, day)
                datePicker.datePicker.minDate = System.currentTimeMillis()
                datePicker.show()
            }
            val initialCalendar = Calendar.getInstance()
            val initialDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val initialDate = initialDateFormat.format(initialCalendar.time)
            todoBinding.date.setText(initialDate)

            //time
            todoBinding.time.setOnClickListener {
                val hour = calendar.get(Calendar.HOUR_OF_DAY)  // Use HOUR_OF_DAY for 24-hour format
                val minute = calendar.get(Calendar.MINUTE)

                val timePicker = TimePickerDialog(requireContext(), { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    calendar.set(Calendar.MINUTE, selectedMinute)
                    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val time = simpleDateFormat.format(calendar.time)
                    todoBinding.time.setText(time)
                }, hour, minute, true)
                timePicker.show()
            }
            val initialTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val initialTime = initialTimeFormat.format(initialCalendar.time)
            todoBinding.time.setText(initialTime)

            //submit
            todoBinding.submit.setOnClickListener {
                val note: String = todoBinding.note.text.toString()
                val selectedDate = todoBinding.date.text.toString()
                val selectedTime = todoBinding.time.text.toString()
                val todoEntity = TodoEntity(0, note, selectedDate, selectedTime)
                viewModel.vmInsertTodo(todoEntity)
                Toast.makeText(requireContext(), "Reminder set successfully", Toast.LENGTH_SHORT).show()

                scheduleAlarm(note, calendar)
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

        todoAdapter = TodoAdapter(requireContext(), arrayListOf())
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.recyclerViewTodos.layoutManager = layoutManager
        binding.recyclerViewTodos.adapter = todoAdapter

        viewModel.todoList.observe(viewLifecycleOwner) { todos ->
            todos?.let {
                todoAdapter.updateTodos(it)
            }
        }

        itemTouchHelper()
        observeTodos()
        return binding.root
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(note: String, calendar: Calendar) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("TITLE", note)
        }
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }


    fun itemTouchHelper() {
        // Implement swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Define your threshold value
                val swipeThreshold = 10 // Adjust this value as needed
                // Calculate the absolute distance swiped
                val dX = viewHolder.itemView.translationX
                val absDX = abs(dX)
                // Check if the swipe distance exceeds the threshold
                if (absDX > swipeThreshold) {
                    // Perform the swipe action only if it exceeds the threshold
                    val position = viewHolder.adapterPosition
                    val todoEntity = todoAdapter.todoList[position]
                    viewModel.vmDeleteTodo(todoEntity)
                    todoAdapter.notifyItemRemoved(position)

                    // Reset the view after deletion
                    val layoutManager = binding.recyclerViewTodos.layoutManager as LinearLayoutManager
                    val position2 = layoutManager.findFirstVisibleItemPosition()
                    val view = layoutManager.findViewByPosition(position2)
                    val topOffset = view?.top ?: 0
                    binding.recyclerViewTodos.post {
                        layoutManager.scrollToPositionWithOffset(position, topOffset)
                        layoutManager.scrollToPosition(0)
                    }
                    // Add code to reset the view after deletion if needed
                    // For example, you might want to reset any changes made during swipe
                } else {
                    // If the swipe distance does not exceed the threshold, reset the view
                    viewHolder.itemView.translationX = 0f
                }
            }
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_24)
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon.intrinsicHeight

                // Define the threshold after which the delete icon sticks
                val stickyThreshold = 300 // Adjust this value as needed
                // Calculate the absolute distance swiped
                val absDX = abs(dX)
                // Gradually reveal the delete icon based on the swipe distance
                val deleteIconAlpha = 1f.coerceAtMost(absDX / stickyThreshold)

                // Calculate the position of the delete icon based on the swipe direction
                if (dX > 0) { // Swiping to the right
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + deleteIcon.intrinsicWidth
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                } else if (dX < 0) { // Swiping to the left
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }
                // Draw the delete icon with alpha based on the swipe distance
                deleteIcon.alpha = (deleteIconAlpha * 255).toInt()
                deleteIcon.draw(c)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodos)
    }

    fun observeTodos(){
        viewModel.getAllTodos().observe(viewLifecycleOwner, Observer {
            todoAdapter.updateTodos(it)
        })
    }

}
