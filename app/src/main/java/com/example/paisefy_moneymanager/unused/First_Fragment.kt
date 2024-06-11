package com.example.paisefy_moneymanager.unused

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.paisefy_moneymanager.R


class First_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

}



//package com.example.paisefy_moneymanager.views.fragments
//
//import android.icu.util.Calendar
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import com.anychart.AnyChart
//import com.anychart.chart.common.dataentry.DataEntry
//import com.anychart.chart.common.dataentry.ValueDataEntry
//import com.anychart.enums.Align
//import com.anychart.enums.LegendLayout
//import com.example.paisefy_moneymanager.databinding.FragmentStatsBinding
//import com.example.paisefy_moneymanager.model.CategoryCount
//import com.example.paisefy_moneymanager.utils.Helper
//import com.example.paisefy_moneymanager.viewmodel.MainViewModel
//import com.example.paisefy_moneymanager.views.activities.MainActivity
//import com.google.android.material.tabs.TabLayout
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class StatsFragment : Fragment() {
//    private lateinit var binding: FragmentStatsBinding
//    private lateinit var calendar: Calendar
//    private lateinit var viewModel: MainViewModel
//    private var milliseconds: Long = 0
//    private lateinit var formattedDate: String
//    private lateinit var formattedMonth: String
//    private var selectedTab = 0 // 0 for Daily, 1 for Monthly
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        binding = FragmentStatsBinding.inflate(inflater, container, false)
//        viewModel = (activity as MainActivity).viewModel
//
//        // Initialize calendar and date formatting
//        calendar = Calendar.getInstance()
//        updateDate() // Update to today's date
//        setupInitialDateFormats()
//
//        // Set visibility based on the initial date
//        checkUpcomingDatesVisibility()
//
//        // Setup click listeners for date navigation
//        setupDateNavigation()
//
//        // Initialize TabLayout
//        setupTabLayout()
//
//        // Initialize the chart view
//        initializePieChart()
//
//        return binding.root
//    }
//
//    private fun setupInitialDateFormats() {
//        val currentTimeMillis = System.currentTimeMillis()
//        val currentDate = Date(currentTimeMillis)
//        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//        formattedDate = dateFormat.format(currentDate)
//        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
//        formattedMonth = monthFormat.format(currentDate)
//    }
//
//    private fun checkUpcomingDatesVisibility() {
//        if (Helper.formateDate(calendar.time).equals(formattedDate)) {
//            binding.upcomingDates.visibility = View.INVISIBLE
//        }
//        if (Helper.formateDateByMonth(calendar.time).equals(formattedMonth)) {
//            binding.upcomingDates.visibility = View.INVISIBLE
//        }
//    }
//
//    private fun setupDateNavigation() {
//        binding.upcomingDates.setOnClickListener {
//            if (selectedTab == 0) {
//                calendar.add(Calendar.DATE, 1)
//                updateDate()
//                if (Helper.formateDate(calendar.time).equals(formattedDate)) {
//                    binding.upcomingDates.visibility = View.INVISIBLE
//                }
//            } else {
//                calendar.add(Calendar.MONTH, 1)
//                updateMonth()
//            }
//        }
//        binding.previousDates.setOnClickListener {
//            if (selectedTab == 0) {
//                calendar.add(Calendar.DATE, -1)
//                updateDate()
//                if (Helper.formateDate(calendar.time) != formattedDate) {
//                    binding.upcomingDates.visibility = View.VISIBLE
//                }
//            } else {
//                calendar.add(Calendar.MONTH, -1)
//                updateMonth()
//            }
//        }
//    }
//
//    private fun setupTabLayout() {
//        binding.tabLayout.apply {
//            addTab(newTab().setText("Daily"))
//            addTab(newTab().setText("Monthly"))
//        }
//
//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                selectedTab = tab.position
//                if (selectedTab == 0) {
//                    updateDate()
//                } else {
//                    updateMonth()
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                // Handle tab unselected
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                // Handle tab reselected
//            }
//        })
//    }
//
//    private fun initializePieChart() {
//        val pie = AnyChart.pie()
//
//        // Dummy initial data
//        val data: MutableList<DataEntry> = ArrayList()
//        data.add(ValueDataEntry("Salary", 3))
//        data.add(ValueDataEntry("Business", 3))
//        data.add(ValueDataEntry("Investment", 3))
//        data.add(ValueDataEntry("Loan", 0))
//        data.add(ValueDataEntry("Rent", 0))
//
//        pie.data(data)
//        pie.labels().position("inside")
//        pie.legend()
//            .padding(0.0, 0.0, 10.0, 0.0)
//            .position("center-bottom")
//            .itemsLayout(LegendLayout.HORIZONTAL)
//            .align(Align.CENTER)
//
//        binding.anyChartView.setChart(pie)
//    }
//
//    private fun updateDate() {
//        // Format the date and update the TextView
//        binding.dateMain.text = Helper.formateDate(calendar.time)
//        val dateString = Helper.formateDate(calendar.time)
//        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//        val date = dateFormat.parse(dateString)
//        milliseconds = date?.time ?: -1
//        // Update categories for the specific date
//        updateCategoriesForDate("INCOME",milliseconds)
//    }
//
//    private fun updateMonth() {
//        binding.dateMain.text = Helper.formateDateByMonth(calendar.time)
//        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
//        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
//        val selectedMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based
//        val selectedYear = calendar.get(Calendar.YEAR)
//
//        updateCategoriesForMonth(selectedMonth, selectedYear)
//
//        if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear) {
//            binding.upcomingDates.visibility = View.INVISIBLE
//        } else {
//            binding.upcomingDates.visibility = View.VISIBLE
//        }
//    }
//
//    private fun updateCategoriesForDate(type: String,date: Long) {
//        // Observe income category counts for the specific date
//        viewModel.getIncomeCategoriesByDate("INCOME",date).observe(viewLifecycleOwner, Observer { incomeCategories ->
//            Log.d("StatsFragment", "Income Categories: $incomeCategories")
//            updatePieChartData(incomeCategories, "Income")
//        })
//
//        // Observe expense category counts for the specific date
//        viewModel.getExpenseCategoriesByDate(date).observe(viewLifecycleOwner, Observer { expenseCategories ->
//            Log.d("StatsFragment", "Expense Categories: $expenseCategories")
//            updatePieChartData(expenseCategories, "Expense")
//        })
//    }
//
//    private fun updateCategoriesForMonth(month: Int, year: Int) {
//        // Observe income category counts for the specific month
//        viewModel.getIncomeCategoriesByMonth(month, year).observe(viewLifecycleOwner, Observer { incomeCategories ->
//            Log.d("StatsFragment", "Income Categories for $month/$year: $incomeCategories")
//            updatePieChartData(incomeCategories, "Income")
//        })
//
//        // Observe expense category counts for the specific month
//        viewModel.getExpenseCategoriesByMonth(month, year).observe(viewLifecycleOwner, Observer { expenseCategories ->
//            Log.d("StatsFragment", "Expense Categories for $month/$year: $expenseCategories")
//            updatePieChartData(expenseCategories, "Expense")
//        })
//    }
//
//    private fun updatePieChartData(categoryCounts: List<CategoryCount>, type: String) {
//        val data: MutableList<DataEntry> = ArrayList()
//
//        categoryCounts.forEach { categoryCount ->
//            // Log each category and count to ensure all data is included
//            Log.d("StatsFragment", "Category: ${categoryCount.category}, Count: ${categoryCount.count}")
//            data.add(ValueDataEntry(categoryCount.category, categoryCount.count))
//        }
//
//        val pie = AnyChart.pie()
//        pie.data(data)
//        pie.labels().position("inside")
//        pie.legend()
//            .padding(0.0, 0.0, 10.0, 0.0)
//            .position("center-bottom")
//            .itemsLayout(LegendLayout.HORIZONTAL)
//            .align(Align.CENTER)
//
//        // Update the chart with the new data
//        binding.anyChartView.setChart(pie)
//        pie.title("$type Categories")
//    }
//}

