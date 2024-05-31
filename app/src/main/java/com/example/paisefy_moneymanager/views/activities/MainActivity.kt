package com.example.paisefy_moneymanager.views.activities

import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.adapter.TransactionsAdapter
import com.example.paisefy_moneymanager.databinding.ActivityMainBinding
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Helper
import com.example.paisefy_moneymanager.viewmodel.MainViewModel
import com.example.paisefy_moneymanager.views.fragments.Add_Transcation_Fragment

import java.util.Date

class MainActivity : AppCompatActivity() {
    lateinit var calendar: Calendar
    //ViewModel
    lateinit var viewModel: MainViewModel
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
        //status bar color
        val window: Window = getWindow()
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.themeColor)

        //ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        //toolbar


        //calendar
        calendar = Calendar.getInstance()
        updateDate()//Today's date

        //date toggle on click
        binding.upcomingDates.setOnClickListener {
            calendar.add(Calendar.DATE, 1)
            updateDate()
        }
        binding.previousDates.setOnClickListener {
            calendar.add(Calendar.DATE, -1)
            updateDate()
        }

        //floating button
        binding.floatingActionButton.setOnClickListener {
            Add_Transcation_Fragment().show(supportFragmentManager, null)
        }

        var transactionArrayList = ArrayList<Transaction>()

        transactionArrayList.add(Transaction(2, "EXPENSE", "Rent", "Bank", "hey there", Date(), 7756.0))
        transactionArrayList.add(Transaction(2, "INCOME", "Other", "UPI", "hey there", Date(), 3355.0))
        transactionArrayList.add(Transaction(2, "INCOME", "Salary", "Cash", "hey there", Date(), 1126.0))
        transactionArrayList.add(Transaction(2, "INCOME", "Investment", "Others", "hey there", Date(), 500.0))
        transactionArrayList.add(Transaction(2, "EXPENSE", "Loan", "Others", "hey there", Date(), 5700.0))
        transactionArrayList.add(Transaction(2, "INCOME", "Other", "Others", "hey there", Date(), 12250.0))
        transactionArrayList.add(Transaction(2, "EXPENSE", "Rent", "UPI", "hey there", Date(), 932.0))
        transactionArrayList.add(Transaction(2, "EXPENSE", "Loan", "Others", "hey there", Date(), 126.0))
        transactionArrayList.add(Transaction(2, "INCOME", "Business", "Cash", "hey there", Date(), 57.0))
        transactionArrayList.add(Transaction(2, "INCOME", "Other", "Bank", "hey there", Date(), 3500.0))

        val transactionAdapter = TransactionsAdapter(applicationContext, transactionArrayList)
        binding.transactionRecyclerMain.layoutManager = LinearLayoutManager(applicationContext)
        binding.transactionRecyclerMain.adapter = transactionAdapter
    }

    //menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    fun updateDate(){
        binding.dateMain.text = Helper.formateDate(calendar.time)
    }
}