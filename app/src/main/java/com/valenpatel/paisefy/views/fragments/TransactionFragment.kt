package com.valenpatel.paisefy.views.fragments

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.adapter.SwipeGesture
import com.valenpatel.paisefy.adapter.TransactionsAdapter
import com.valenpatel.paisefy.databinding.FragmentTransactionBinding
import com.valenpatel.paisefy.model.Transaction
import com.valenpatel.paisefy.utils.Helper
import com.valenpatel.paisefy.viewmodel.MainViewModel
import com.valenpatel.paisefy.views.activities.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TransactionFragment : Fragment(), TransactionsAdapter.OnItemClickedListener  {

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

        observeTransactions()
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

        transactionAdapter = TransactionsAdapter(requireContext(), ArrayList(), this)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.transactionRecyclerMain.layoutManager = layoutManager
        binding.transactionRecyclerMain.adapter = transactionAdapter

        swipeToGesture(binding.transactionRecyclerMain)


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
                    binding.noRecordForDates.visibility = View.GONE
                    binding.dateMain.visibility = View.VISIBLE
                } else if (tab.text == "All transaction") {
                    selectedtab = 2
                    updateGenericAllTransactions()
                    binding.frame.visibility = View.GONE
                    binding.previousDates.visibility = View.INVISIBLE
                    binding.dateMain.visibility = View.VISIBLE
                    binding.noRecordForDates.visibility = View.GONE
                    binding.noRecordForMonth.visibility = View.GONE
                    binding.upcomingDates.visibility = View.INVISIBLE
                    binding.dateMain.visibility = View.INVISIBLE
                } else if (tab.text == "Reminders") {
                    selectedtab = 3
                    removeContentOnSummaryTabClicked()
                    binding.frame.visibility = View.VISIBLE
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, NotesFragment()).commit()
                    binding.dateMain.visibility = View.INVISIBLE
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
        viewModel.getTransactionsByMonth(month, year)
            .observe(getViewLifecycleOwner(), Observer { transactions ->
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
            if (it.isNotEmpty()) {
                binding.lottieAnimationViewMain.visibility = View.GONE
                binding.noRecordForDates.visibility = View.GONE
                binding.noRecordForMonth.visibility = View.GONE
            }
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

    fun swipeToGesture(transactionRecyclerMain: RecyclerView) {
        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                try {
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            val deletedTransaction = transactionAdapter.transactions[position]

                            // Remove item from the adapter's dataset
                            transactionAdapter.transactions.removeAt(position)
                            transactionAdapter.notifyItemRemoved(position)

                            // Show Snackbar with UNDO option
                            val snackbar = Snackbar.make(binding.transactionRecyclerMain, "Transaction Deleted", Snackbar.LENGTH_LONG
                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                var isUndoClicked = false

                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    super.onDismissed(transientBottomBar, event)
                                    if (!isUndoClicked) {
                                        // User did not click UNDO, perform delete operation
                                        viewModel.insertRecentlyDeletedTransaction(deletedTransaction)
                                        viewModel.vmDeleteTransaction(deletedTransaction)
                                    }
                                }

                                override fun onShown(transientBottomBar: Snackbar?) {
                                    transientBottomBar?.setAction("UNDO") {
                                        isUndoClicked = true
                                        // Re-insert the transaction
                                        transactionAdapter.transactions.add(position, deletedTransaction)
                                        transactionAdapter.notifyItemInserted(position)
                                    }
                                    super.onShown(transientBottomBar)
                                }
                            }).apply {
                                animationMode = Snackbar.ANIMATION_MODE_FADE
                            }

                            snackbar.setActionTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.dark_orange
                                )
                            )
                            snackbar.show()
                        }

                        ItemTouchHelper.RIGHT -> {
                            val archivedTransaction = transactionAdapter.transactions[position]

                            // Remove item from the adapter's dataset
                            transactionAdapter.transactions.removeAt(position)
                            transactionAdapter.notifyItemRemoved(position)

                            // Show Snackbar with UNDO option
                            val snackbar = Snackbar.make(binding.transactionRecyclerMain, "Transaction Archived", Snackbar.LENGTH_LONG
                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                var isUndoClicked = false

                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    super.onDismissed(transientBottomBar, event)
                                    if (!isUndoClicked) {
                                        // User did not click UNDO, perform archive operation
                                        viewModel.insertArchivedTransaction(archivedTransaction)
                                    }
                                }

                                override fun onShown(transientBottomBar: Snackbar?) {
                                    transientBottomBar?.setAction("UNDO") {
                                        isUndoClicked = true
                                        // Re-insert the transaction
                                        transactionAdapter.transactions.add(position, archivedTransaction)
                                        transactionAdapter.notifyItemInserted(position)
                                    }
                                    super.onShown(transientBottomBar)
                                }
                            }).apply {
                                animationMode = Snackbar.ANIMATION_MODE_FADE
                            }

                            snackbar.setActionTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.dark_orange
                                )
                            )
                            snackbar.show()
                        }
                    }
                } catch (exception: Exception) {
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(transactionRecyclerMain)
    }

        private fun shrinkFab() {
            binding.floatingActionButton.shrink()
        }

        private fun expandFab() {
            binding.floatingActionButton.extend()
        }


    override fun onItemClicked(transaction: Transaction) {
        val bundle = Bundle().apply {
            putParcelable("transaction", transaction)
        }
        val secondFragment = ViewTransactionFragment().apply {
            arguments = bundle
        }
        (activity as MainActivity).getFragment(secondFragment)
    }
}


