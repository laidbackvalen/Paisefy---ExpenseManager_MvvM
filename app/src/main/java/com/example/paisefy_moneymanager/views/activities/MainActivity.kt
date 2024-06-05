package com.example.paisefy_moneymanager.views.activities

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.icu.util.Calendar
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.adapter.TransactionsAdapter
import com.example.paisefy_moneymanager.databinding.ActivityMainBinding
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Constants
import com.example.paisefy_moneymanager.utils.Helper
import com.example.paisefy_moneymanager.viewmodel.MainViewModel
import com.example.paisefy_moneymanager.views.fragments.Add_Transcation_Fragment
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    lateinit var calendar: Calendar

    //ViewModel
    lateinit var viewModel: MainViewModel
    private lateinit var transactionAdapter: TransactionsAdapter
    var milliseconds: Long = 0
    private lateinit var formattedDate: String
    private lateinit var formattedMonth: String
    //Tab
    var selectedtab = 0

    //view binding
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        statusBar()

        // Shrink the FloatingActionButton initially
       // binding.floatingActionButton.shrink()

        //on Nested Scroll //FOR FLAOTING BUTTON
        binding.nestedScrollMain.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY + 12 && binding.floatingActionButton.isExtended()) {
                binding.floatingActionButton.shrink()
            }
            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !binding.floatingActionButton.isExtended()) {
                binding.floatingActionButton.extend()
            }
            // if the nestedScrollView is at the first item of the list then the
            // extended floating action should be in extended state
            if (scrollY == 0) {
                binding.floatingActionButton.extend()
            }
        }

        //ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

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
            if (selectedtab==0) {
                calendar.add(Calendar.DATE, 1)
                updateDate()
                if (Helper.formateDate(calendar.time).equals(formattedDate)) {
                    binding.upcomingDates.visibility = View.INVISIBLE
                }
            }else{
                calendar.add(Calendar.MONTH, 1)
                updateMonth()
            }

        }
        binding.previousDates.setOnClickListener {
            if(selectedtab==0) {
                calendar.add(Calendar.DATE, -1)
                updateDate()
                if (Helper.formateDate(calendar.time) != formattedDate) {
                    binding.upcomingDates.visibility = View.VISIBLE
                }
            }else{
                calendar.add(Calendar.MONTH, -1)
                updateMonth()
            }
        }
        //floating button
        binding.floatingActionButton.setOnClickListener {
            binding.floatingActionButton.show()
            Add_Transcation_Fragment().show(supportFragmentManager, null)
        }

        binding.dateMain.setOnClickListener {
            showDatePicker()
        }

        setupClickListeners()

        transactionAdapter = TransactionsAdapter(applicationContext, ArrayList())
        binding.transactionRecyclerMain.layoutManager = LinearLayoutManager(applicationContext)
        binding.transactionRecyclerMain.adapter = transactionAdapter


        itemTouchHelper()

//        val date = Date()
//        Toast.makeText(applicationContext, ""+date, Toast.LENGTH_SHORT).show()

        //TabLayout
        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!!.text == "Monthly"){
                  selectedtab=1
                    updateMonth()
                }else if(tab.text == "Daily"){
                    selectedtab = 0
                    updateDate()
                    updateTextViewForToday()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    //menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
    fun updateMonth(){
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
        viewModel.getTransactionsByMonth(month, year).observe(this, Observer { transactions ->
            transactionAdapter.updateTransactions(transactions)
            updateIncomeExpenseTotals(transactions)
        })
    }

    private fun updateIncomeExpenseTotals(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            binding.lottieAnimationViewMain.visibility = View.VISIBLE
        } else {
            binding.lottieAnimationViewMain.visibility = View.GONE
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
        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            updateDate()
            updateTransactionsForDate(milliseconds)
        }, year, month, day)
        datePicker.datePicker.maxDate = System.currentTimeMillis()

        if (Helper.formateDate(calendar.time).equals(formattedDate)) {
            binding.upcomingDates.visibility = View.INVISIBLE
        }else{
            binding.upcomingDates.visibility = View.VISIBLE
        }

        if (Helper.formateDateByMonth(calendar.time).equals(formattedMonth)) {
            binding.upcomingDates.visibility = View.INVISIBLE
        }else{
            binding.upcomingDates.visibility = View.VISIBLE
        }

        datePicker.show()
    }

    private fun observeTransactions() {
        viewModel.transactionList.observe(this, Observer { transactions ->
            transactionAdapter.updateTransactions(transactions)
            if (transactions.isEmpty()) {
                binding.lottieAnimationViewMain.visibility = View.VISIBLE
            } else {
                binding.lottieAnimationViewMain.visibility = View.GONE
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
        viewModel.getTransactionsByDate(date).observe(this, Observer { transactions ->
            // Reset amounts
            var incomeAmount = 0.0
            var expenseAmount = 0.0

            if (transactions.isEmpty()) {
                binding.lottieAnimationViewMain.visibility = View.VISIBLE
            } else {
                binding.lottieAnimationViewMain.visibility = View.GONE
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
        viewModel.transactionList.observe(this, Observer {
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
            }
            else if(selectedtab==0){
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
        val widthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp.toFloat(), resources.displayMetrics).toInt()
        val params = view.layoutParams
        params.width = widthInPx
        view.layoutParams = params
    }

    fun statusBar(){
        //status bar color
        val window: Window = getWindow()
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.themeColor)
    }



    fun itemTouchHelper(){
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

                val deleteIcon = ContextCompat.getDrawable(applicationContext, R.drawable.baseline_delete_24)
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
}
