package com.example.paisefy_moneymanager.views.fragments

import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.databinding.FragmentStatsBinding
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Helper
import com.example.paisefy_moneymanager.viewmodel.MainViewModel
import com.example.paisefy_moneymanager.views.activities.MainActivity
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Locale

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding
    private lateinit var calendar: Calendar
    private lateinit var viewModel: MainViewModel
    private var milliseconds: Long = 0
    private var selectedTab = 0 // 0 for Daily, 1 for Monthly
    private lateinit var incomeExpenseType: String
    private lateinit var pieChart: AnyChartView // Declare AnyChartView
    private lateinit var pie: Pie // Declare Pie chart object

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel

        // Initialize calendar
        calendar = Calendar.getInstance()
        incomeExpenseType = binding.incomeButton.text.toString()
        binding.incomeButton.setTextColor(Color.WHITE)
        binding.incomeButton.setBackgroundResource(R.drawable.bg_income)
        binding.expenseButton.setTextColor(resources.getColor(R.color.red))
        binding.expenseButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_expense)
        // Initialize the chart view
        pieChart = binding.anyChartView // Assign the AnyChartView

        // Set up the pie chart
        setupPieChart()

        // Initialize UI components
        setupUI()

        // Initialize TabLayout
        setupTabLayout()

        observeData()
        return binding.root
    }

    private fun setupUI() {
        // Update UI based on current date
        updateDate()

        // Set up click listeners for date navigation
        binding.upcomingDates.setOnClickListener {
            navigateToNextDate()
        }

        binding.previousDates.setOnClickListener {
            navigateToPreviousDate()
        }

        // Set up initial button listeners for daily view
        setupButtonListenersForDaily()
    }

    private fun setupTabLayout() {
        binding.tabLayout.apply {
            addTab(newTab().setText("Daily"))
            addTab(newTab().setText("Monthly"))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTab = tab.position
                if (selectedTab == 0) {
                    setupButtonListenersForDaily()
                    updateDate()
                    Toast.makeText(requireContext(), "" + selectedTab, Toast.LENGTH_SHORT).show()
                } else {
                    setupButtonListenersForMonthly()
                    updateMonth()
                    Toast.makeText(requireContext(), "" + selectedTab, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupButtonListenersForDaily() {
        binding.incomeButton.setOnClickListener {
            incomeExpenseType = binding.incomeButton.text.toString()
            binding.incomeButton.setTextColor(Color.WHITE)
            binding.incomeButton.setBackgroundResource(R.drawable.bg_income)
            binding.expenseButton.setTextColor(resources.getColor(R.color.red))
            binding.expenseButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_expense)
            updateDate()
        }

        binding.expenseButton.setOnClickListener {
            incomeExpenseType = binding.expenseButton.text.toString()
            binding.expenseButton.setTextColor(Color.WHITE)
            binding.expenseButton.setBackgroundResource(R.drawable.bg_expense)
            binding.incomeButton.setTextColor(resources.getColor(R.color.income))
            binding.incomeButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_income)
            updateDate()
        }
    }

    private fun setupButtonListenersForMonthly() {
        binding.incomeButton.setOnClickListener {
            incomeExpenseType = binding.incomeButton.text.toString()
            binding.incomeButton.setTextColor(Color.WHITE)
            binding.incomeButton.setBackgroundResource(R.drawable.bg_income)
            binding.expenseButton.setTextColor(resources.getColor(R.color.red))
            binding.expenseButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_expense)
            updateMonth()
        }

        binding.expenseButton.setOnClickListener {
            incomeExpenseType = binding.expenseButton.text.toString()
            binding.expenseButton.setTextColor(Color.WHITE)
            binding.expenseButton.setBackgroundResource(R.drawable.bg_expense)
            binding.incomeButton.setTextColor(resources.getColor(R.color.income))
            binding.incomeButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_income)
            updateMonth()
        }
    }

    private fun setupPieChart() {
        // Initialize the Pie chart object
        pie = AnyChart.pie()

        // Set initial properties for the pie chart
        pie.labels().position("inside")
        pie.legend()
            .padding(0.0, 0.0, 10.0, 0.0)
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)

        // Set the chart to the AnyChartView
        pieChart.setChart(pie)
    }

    private fun observeData() {
        viewModel.transactionList.observe(viewLifecycleOwner, Observer {
            updateDate()
        })
    }

    private fun updateDate() {
        // Update UI with the current date
        binding.dateMain.text = Helper.formateDate(calendar.time)
        val dateString = Helper.formateDate(calendar.time)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        milliseconds = date?.time ?: -1

        // Update the pie chart with data for the selected date
        updateChartData()

        // Hide the "Next" button if the selected date is the current date
        val today = Calendar.getInstance()
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        ) {
            binding.upcomingDates.visibility = View.INVISIBLE
        } else {
            binding.upcomingDates.visibility = View.VISIBLE
        }
    }

    private fun updateMonth() {
        // Update UI with the current month
        binding.dateMain.text = Helper.formateDateByMonth(calendar.time)

        // Fetch transactions for the current month
        fetchMonthlyTransactions()

        // Hide the "Next" button if the current month is selected
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
            .observe(viewLifecycleOwner, Observer { transactions ->
                // Directly update the chart data with the fetched transactions
                updateMonthlyChartData(transactions)
            })
    }

    private fun updateChartData() {
        viewModel.getTransactionsByDate(milliseconds).observe(viewLifecycleOwner, Observer { transactions ->

            val data: MutableList<DataEntry> = ArrayList()
            val incomeCategoryCounts = mutableMapOf<String, Int>()
            val expenseCategoryCounts = mutableMapOf<String, Int>()

            Log.d("StatsFragment", "Transactions for date: $milliseconds, Count: ${transactions.size}")

            if (transactions.isEmpty()) {
                binding.lottieAnimationViewMain.visibility = View.VISIBLE
                binding.anyChartView.visibility = View.INVISIBLE
            } else {
                binding.lottieAnimationViewMain.visibility = View.GONE
                binding.anyChartView.visibility = View.VISIBLE
            }

            // Aggregate counts for income and expense categories
            transactions.forEach { transaction ->
                val category = transaction.category
                val amount = transaction.amount

                when (transaction.transType) {
                    "INCOME" -> {
                        incomeCategoryCounts[category] =
                            ((incomeCategoryCounts[category] ?: 0) + amount).toInt()
                    }

                    "EXPENSE" -> {
                        expenseCategoryCounts[category] =
                            ((expenseCategoryCounts[category] ?: 0) + amount).toInt()
                    }
                }
            }

//            // Check if there's no income data and add expense data
//            if (incomeCategoryCounts.isEmpty() && expenseCategoryCounts.isNotEmpty()) {
//                incomeExpenseType = "EXPENSE"
//                binding.expenseButton.setTextColor(Color.WHITE)
//                binding.expenseButton.setBackgroundResource(R.drawable.bg_expense)
//                binding.incomeButton.setTextColor(resources.getColor(R.color.income))
//                binding.incomeButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_income)
//
//            }

            if (incomeExpenseType == "INCOME") {
                // Add aggregated income data to the list
                incomeCategoryCounts.forEach { (category, totalAmount) ->
                    data.add(ValueDataEntry(category, totalAmount))
                    updatePieChartData(data)

                }
            } else if (incomeExpenseType == "EXPENSE") {
                // Add aggregated expense data to the list
                expenseCategoryCounts.forEach { (category, totalAmount) ->
                    data.add(ValueDataEntry(category, totalAmount))
                    updatePieChartData(data)
                }
            }
            Log.d("StatsFragmentPieChartData", "Pie chart data: $data")

          //  updatePieChartData(data)
        })
    }

    private fun updateMonthlyChartData(transactions: List<Transaction>) {
        // Use a map to accumulate amounts for each category
        val incomeCategoryCounts = mutableMapOf<String, Int>()
        val expenseCategoryCounts = mutableMapOf<String, Int>()

        Log.d("StatsFragment", "Transactions for month, Count: ${transactions.size}")

        if (transactions.isEmpty()) {
            binding.lottieAnimationViewMain.visibility = View.VISIBLE
        } else {
            binding.lottieAnimationViewMain.visibility = View.GONE
            binding.anyChartView.visibility = View.VISIBLE
        }

        // Iterate over the transactions and sum the amounts for each category
        transactions.forEach { transaction ->
            val category = transaction.category
            val amount = transaction.amount

            when (transaction.transType) {
                "INCOME" -> {
                    // Sum the amounts for each income category
                    incomeCategoryCounts[category] =
                        (incomeCategoryCounts[category] ?: 0) + amount.toInt()
                }

                "EXPENSE" -> {
                    // Sum the amounts for each expense category
                    expenseCategoryCounts[category] =
                        (expenseCategoryCounts[category] ?: 0) + amount.toInt()
                }
            }
        }

        // Create a list of DataEntry for the pie chart
        val data: MutableList<DataEntry> = ArrayList()
        if (incomeExpenseType == "INCOME") {
            // Add aggregated income data to the list
            incomeCategoryCounts.forEach { (category, totalAmount) ->
                data.add(ValueDataEntry(category, totalAmount))
                updatePieChartData(data)
            }
        } else if (incomeExpenseType == "EXPENSE") {
            // Add aggregated expense data to the list
            expenseCategoryCounts.forEach { (category, totalAmount) ->
                data.add(ValueDataEntry(category, totalAmount))
                updatePieChartData(data)
            }
        }
        Log.d("StatsFragment", "Pie chart data: $data")

     //   updatePieChartData(data)
    }

    private fun updatePieChartData(data: List<DataEntry>) {
        if (::pie.isInitialized && ::pieChart.isInitialized) { // Ensure the pie and pieChart are initialized
            try {
                pie.data(data) // Set the aggregated data to the pie chart
                Log.d("StatsFragmentData", "Pie chart data: $data")
                pie.title("Transactions by Category")
                pie.legend().title().enabled(true)
                pie.legend().title()
                    .text(" ")
                    .padding(0.0, 0.0, 10.0, 0.0)
                pie.labels().position("inside")
                pie.legend()
                    .padding(0.0, 0.0, 10.0, 0.0)
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER)
            } catch (e: Exception) {
                // Handle any exceptions that might occur during data update
                Toast.makeText(requireContext(), "Error updating chart data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // In case pie or pieChart is not initialized
            Toast.makeText(requireContext(), "Error: Pie chart or view is not initialized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNextDate() {
        val today = Calendar.getInstance()
        if (selectedTab == 0) {
            if (calendar.before(today)) {
                calendar.add(Calendar.DATE, 1)
                updateDate()
            }
        } else {
            calendar.add(Calendar.MONTH, 1)
            updateMonth()
        }
    }

    private fun navigateToPreviousDate() {
        if (selectedTab == 0) {
            calendar.add(Calendar.DATE, -1)
            updateDate()
        } else {
            calendar.add(Calendar.MONTH, -1)
            updateMonth()
        }
    }
}