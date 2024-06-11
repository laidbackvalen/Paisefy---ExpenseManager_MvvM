package com.example.paisefy_moneymanager.views.fragments

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.icu.util.Calendar
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.adapter.TransactionsAdapter
import com.example.paisefy_moneymanager.databinding.FragmentTransactionBinding
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Helper
import com.example.paisefy_moneymanager.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TransactionFragment : Fragment() {

    lateinit var binding: FragmentTransactionBinding
    lateinit var calendar: Calendar
    private lateinit var transactionAdapter: TransactionsAdapter

    //ViewModel
    lateinit var viewModel: MainViewModel
    var milliseconds: Long = 0
    private lateinit var formattedDate: String
    private lateinit var formattedMonth: String

    //Tab
    var selectedtab = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionBinding.inflate(layoutInflater)

        //ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.nestedScrollMain.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                shrinkFab()
            } else {
                expandFab()
            }
        }

        //calendar
        calendar = Calendar.getInstance()
        updateDate()//Today's date

        val currentTimeMillis = System.currentTimeMillis()
        val currentDate = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formattedDate = dateFormat.format(currentDate)
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        formattedMonth = monthFormat.format(currentDate)

        if (Helper.formateDate(calendar.time).equals(formattedDate)) {
            binding.upcomingDates.visibility = View.INVISIBLE
            updateDate()
        }

        if (Helper.formateDateByMonth(calendar.time).equals(formattedMonth)) {
            binding.upcomingDates.visibility = View.INVISIBLE
            updateDate()
        }

        //date toggle on click
        binding.upcomingDates.setOnClickListener {
            if (selectedtab == 0) {
                calendar.add(Calendar.DATE, 1)
                updateDate()
                if (Helper.formateDate(calendar.time).equals(formattedDate)) {
                    binding.upcomingDates.visibility = View.INVISIBLE
                }
            } else {
                calendar.add(Calendar.MONTH, 1)
                updateMonth()
            }

        }
        binding.previousDates.setOnClickListener {
            if (selectedtab == 0) {
                calendar.add(Calendar.DATE, -1)
                updateDate()
                if (Helper.formateDate(calendar.time) != formattedDate) {
                    binding.upcomingDates.visibility = View.VISIBLE
                }
            } else {
                calendar.add(Calendar.MONTH, -1)
                updateMonth()
            }
        }
        //floating button
        binding.floatingActionButton.setOnClickListener {
            binding.floatingActionButton.show()
            Add_Transcation_Fragment().show(getParentFragmentManager(), null)
        }

        binding.dateMain.setOnClickListener {
            showDatePicker()
        }

        setupClickListeners()

        transactionAdapter = TransactionsAdapter(requireContext(), ArrayList())
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.transactionRecyclerMain.layoutManager = layoutManager
        binding.transactionRecyclerMain.adapter = transactionAdapter

        itemTouchHelper()

//        val date = Date()
//        Toast.makeText(applicationContext, ""+date, Toast.LENGTH_SHORT).show()
        binding.frame.visibility = View.GONE
        //TabLayout
        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.text == "Monthly") {
                    selectedtab = 1
                    binding.transactionGenric.text = "Monthly"
                    binding.alltransactionImage.setImageResource(R.drawable.arrowdownaz)
                    setViewWidth(binding.allTransactionView, 120)
                    updateMonth()
                    binding.frame.visibility = View.GONE
                    binding.previousDates.visibility = View.VISIBLE
                    binding.dateMain.visibility = View.VISIBLE
                    binding.noRecordForDates.visibility = View.GONE
                } else if (tab.text == "Daily") {
                    selectedtab = 0
                    updateDate()
                    updateTextViewForToday()
                    binding.frame.visibility = View.GONE
                    binding.previousDates.visibility = View.VISIBLE
                    binding.dateMain.visibility = View.VISIBLE
                } else if (tab.text == "All transaction") {
                    selectedtab = 2
                    updateGenericAllTransactions()
                    binding.frame.visibility = View.GONE
                    binding.previousDates.visibility = View.INVISIBLE
                    binding.dateMain.visibility = View.VISIBLE
                    binding.noRecordForDates.visibility = View.GONE
                } else if (tab.text == "Reminders") {
                    selectedtab = 3
                    removeContentOnSummaryTabClicked()
                    binding.frame.visibility = View.VISIBLE
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, NotesFragment()).commit()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


        return binding.root
    }

    private fun removeContentOnSummaryTabClicked() {
        binding.previousDates.visibility = View.INVISIBLE
        binding.upcomingDates.visibility = View.INVISIBLE
        binding.dateMain.text = "Reminders"
    }

    fun updateDate() {
        // Format the date and update the TextView
        binding.dateMain.text = Helper.formateDate(calendar.time)
        val dateString = Helper.formateDate(calendar.time)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        milliseconds = date?.time ?: -1
        // Update transactions for the specific date
        updateTransactionsForDate(milliseconds)

    }

    fun updateMonth() {
        binding.dateMain.text = Helper.formateDateByMonth(calendar.time)
        fetchMonthlyTransactions()
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear) {
            binding.upcomingDates.visibility = View.INVISIBLE
        } else {
            binding.upcomingDates.visibility = View.VISIBLE
        }

    }

    private fun fetchMonthlyTransactions() {
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based
        val year = calendar.get(Calendar.YEAR)
        viewModel.getTransactionsByMonth(month, year).observe(getViewLifecycleOwner(), Observer { transactions ->
                transactionAdapter.updateTransactions(transactions)
                updateIncomeExpenseTotals(transactions)
            })
    }

    private fun updateIncomeExpenseTotals(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            binding.lottieAnimationViewMain.visibility = View.VISIBLE
            binding.noRecordForMonth.visibility = View.VISIBLE
        } else {
            binding.lottieAnimationViewMain.visibility = View.GONE
            binding.noRecordForMonth.visibility = View.GONE
            binding.noRecordForDates.visibility = View.GONE

        }
        var incomeAmount = 0.0
        transactions.forEach {
            if (it.transType == "INCOME") {
                incomeAmount += it.amount
            }
            binding.incomeAmountText.text = "₹ $incomeAmount"
        }
        var expenseAmount = 0.0
        transactions.forEach {
            if (it.transType == "EXPENSE") {
                expenseAmount += it.amount
            }
            binding.expenseAmountText.text = "₹ $expenseAmount"
        }
        var total = incomeAmount - expenseAmount
        binding.totalAmountText.text = "₹ $total"
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePicker =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                updateDate()
                updateTransactionsForDate(milliseconds)
            }, year, month, day)
        datePicker.datePicker.maxDate = System.currentTimeMillis()

        if (Helper.formateDate(calendar.time).equals(formattedDate)) {
            binding.upcomingDates.visibility = View.INVISIBLE
        } else {
            binding.upcomingDates.visibility = View.VISIBLE
        }

        if (Helper.formateDateByMonth(calendar.time).equals(formattedMonth)) {
            binding.upcomingDates.visibility = View.INVISIBLE
        } else {
            binding.upcomingDates.visibility = View.VISIBLE
        }

        datePicker.show()
    }

    private fun observeTransactions() {
        viewModel.transactionList.observe(getViewLifecycleOwner(), Observer { transactions ->
            transactionAdapter.updateTransactions(transactions)
            if (transactions.isEmpty()) {
                binding.lottieAnimationViewMain.visibility = View.VISIBLE
                binding.noRecordForDates.visibility = View.VISIBLE
            } else {
                binding.lottieAnimationViewMain.visibility = View.GONE
                binding.noRecordForDates.visibility = View.GONE
            }
            var incomeAmount = 0.0
            transactions.forEach {
                if (it.transType == "INCOME") {
                    incomeAmount += it.amount
                }
                binding.incomeAmountText.text = "₹ $incomeAmount"
            }
            var expenseAmount = 0.0
            transactions.forEach {
                if (it.transType == "EXPENSE") {
                    expenseAmount += it.amount
                }
                binding.expenseAmountText.text = "₹ $expenseAmount"
            }
            var total = incomeAmount - expenseAmount
            binding.totalAmountText.text = "₹ $total"
        })
    }

    private fun updateTransactionsForDate(date: Long) {
        viewModel.getTransactionsByDate(date).observe(viewLifecycleOwner, Observer { transactions ->
            // Reset amounts
            var incomeAmount = 0.0
            var expenseAmount = 0.0

            if (transactions.isEmpty()) {
                binding.lottieAnimationViewMain.visibility = View.VISIBLE
                binding.noRecordForDates.visibility = View.VISIBLE
                binding.noRecordForMonth.visibility = View.INVISIBLE

            } else {
                binding.lottieAnimationViewMain.visibility = View.GONE
                binding.noRecordForDates.visibility = View.GONE
                binding.noRecordForMonth.visibility = View.GONE
            }

            transactions?.let {
                transactionAdapter.updateTransactions(it)
                // Iterate through transactions and update amounts
                transactions.forEach {
                    when (it.transType) {
                        "INCOME" -> incomeAmount += it.amount
                        "EXPENSE" -> expenseAmount += it.amount
                    }
                }
                // Update UI with new amounts
                binding.incomeAmountText.text = "₹ $incomeAmount"
                binding.expenseAmountText.text = "₹ $expenseAmount"
                val total = incomeAmount - expenseAmount
                binding.totalAmountText.text = "₹ $total"
            }
        })
    }

    private fun updateGenericAllTransactions() {
        binding.transactionGenric.text = "All transactions"
        binding.dateMain.text = "All transactions"
        binding.alltransactionImage.setImageResource(R.drawable.arrowdownaz)
        observeTransactions()
        setViewWidth(binding.allTransactionView, 160)
        viewModel.transactionList.observe(viewLifecycleOwner, Observer {
            transactionAdapter.updateTransactions(it)
        })
    }

    private fun setupClickListeners() {
        binding.allTransactionView.setOnClickListener {
            if (binding.transactionGenric.text == "Daily") {
                updateGenericAllTransactions()
            } else if (binding.transactionGenric.text == "All transactions") {
                updateDate()
                updateTextViewForToday()
            } else if (selectedtab == 0) {
                updateGenericAllTransactions()
            }
        }
    }

    private fun updateTextViewForToday() {
        binding.transactionGenric.text = "Daily"
        binding.alltransactionImage.setImageResource(R.drawable.alltran)
        setViewWidth(binding.allTransactionView, 90)
    }

    private fun setViewWidth(view: View, widthInDp: Int) {
        val widthInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            widthInDp.toFloat(),
            resources.displayMetrics
        ).toInt()
        val params = view.layoutParams
        params.width = widthInPx
        view.layoutParams = params
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
                    val transaction = transactionAdapter.transactions[position]
                    viewModel.vmDeleteTransaction(transaction)
                    transactionAdapter.notifyItemRemoved(position)

                    // Reset the view after deletion
                    val layoutManager = binding.transactionRecyclerMain.layoutManager as LinearLayoutManager
                    val position2 = layoutManager.findFirstVisibleItemPosition()
                    val view = layoutManager.findViewByPosition(position2)
                    val topOffset = view?.top ?: 0
                    binding.transactionRecyclerMain.post {
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
        itemTouchHelper.attachToRecyclerView(binding.transactionRecyclerMain)
    }


    private fun shrinkFab() {
        binding.floatingActionButton.shrink()
    }
    private fun expandFab() {
        binding.floatingActionButton.extend()
    }
}