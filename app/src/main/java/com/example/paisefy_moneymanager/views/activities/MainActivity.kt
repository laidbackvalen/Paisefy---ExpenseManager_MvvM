package com.example.paisefy_moneymanager.views.activities

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.icu.util.Calendar
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
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
import com.example.paisefy_moneymanager.views.fragments.StatsFragment
import com.example.paisefy_moneymanager.views.fragments.TransactionFragment
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

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
        statusBar()
        //ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(R.id.frameContentUnderMain, TransactionFragment()).commit()

        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        //calendar
        calendar = Calendar.getInstance()

        binding.bottomNavigation.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                if (item.itemId == R.id.transaction){
                    getFragment(TransactionFragment())
//                    fragmentTransaction.replace(R.id.frameContentUnderMain, TransactionFragment())
//                    supportFragmentManager.popBackStack()
                }else if(item.itemId == R.id.stats){
                    getFragment(StatsFragment())
//                    fragmentTransaction.replace(R.id.frameContentUnderMain, StatsFragment())
//                    fragmentTransaction.addToBackStack(null)
                }
                    return true
            }
        })

    }

    //menu
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        getMenuInflater().inflate(R.menu.top_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
    fun statusBar() {
        val window: Window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.themeColor)
        window.navigationBarColor = ContextCompat.getColor(applicationContext, R.color.white)
    }
    fun getFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.frameContentUnderMain.id, fragment)
        fragmentTransaction.commit()
    }
}
