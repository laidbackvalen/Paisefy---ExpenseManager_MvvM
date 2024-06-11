package com.example.paisefy_moneymanager.views.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.adapter.AccountsAdapter
import com.example.paisefy_moneymanager.adapter.CategoryAdapter
import com.example.paisefy_moneymanager.databinding.FragmentAddTranscationBinding
import com.example.paisefy_moneymanager.databinding.ListDialogBinding
import com.example.paisefy_moneymanager.model.Account
import com.example.paisefy_moneymanager.model.Category
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Constants
import com.example.paisefy_moneymanager.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.ParseException
import java.util.Date
import java.util.Locale

class Add_Transcation_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTranscationBinding
    val viewModel: MainViewModel by activityViewModels()
    private var selectedTransactionType: String? = null
    private var transactionDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTranscationBinding.inflate(inflater, container, false)

        binding.incomeButton.setOnClickListener {
            binding.incomeButton.setTextColor(Color.WHITE)
            binding.incomeButton.setBackgroundResource(R.drawable.bg_income)
            binding.expenseButton.setTextColor(resources.getColor(R.color.red))
            binding.expenseButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_expense)
            selectTransactiontype("INCOME")
        }
        binding.expenseButton.setOnClickListener {
            binding.expenseButton.setTextColor(Color.WHITE)
            binding.expenseButton.setBackgroundResource(R.drawable.bg_expense)
            binding.incomeButton.setTextColor(resources.getColor(R.color.income))
            binding.incomeButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_income)
            selectTransactiontype("EXPENSE")
        }

        // Date Picker
        binding.date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    val simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
                    val date = simpleDateFormat.format(calendar.time)
                    binding.date.setText(date)
                }, year, month, day)
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
        val date = simpleDateFormat.format(calendar.time)
        binding.date.setText(date)

        //Category
        binding.category.setOnClickListener {
            val lisDialogBinding = ListDialogBinding.inflate(layoutInflater)
            val categoryDialog = AlertDialog.Builder(requireContext()).create()
            categoryDialog.setView(lisDialogBinding.root)

            //Category Adapter //Custom Interface which set the category name
            //Constant ArrayList
            val categoryAdapter = CategoryAdapter(requireContext(), Constants.categoryArrayList, object : CategoryAdapter.CategoryClickListener {
                    override fun onCategoryClick(category: Category) {
                        binding.category.setText(category.categoryName)
                        categoryDialog.dismiss()
                    }
                })
            lisDialogBinding.recyclerViewForReUsingDialog.layoutManager = GridLayoutManager(requireContext(), 3)
            lisDialogBinding.recyclerViewForReUsingDialog.adapter = categoryAdapter
            categoryDialog.show()
        }

        //Account Type
        binding.account.setOnClickListener {
            val lisDialogBinding = ListDialogBinding.inflate(layoutInflater)
            val accountDialog = AlertDialog.Builder(requireContext()).create()
            accountDialog.setView(lisDialogBinding.root)
            //ArrayList
            val accountArrayList = ArrayList<Account>()
            accountArrayList.add(Account(0.0, "Cash"))
            accountArrayList.add(Account(0.0, "Bank"))
            accountArrayList.add(Account(0.0, "UPI"))
            accountArrayList.add(Account(0.0, "Other"))

            //Account Adapter
            val AccountAdapter = AccountsAdapter(requireContext(), accountArrayList, object : AccountsAdapter.AccountTypeClickListener {
                    override fun onAccountTypeClick(accountType: String) {
                        binding.account.setText(accountType)
                        accountDialog.dismiss() //AccountDialog closes
                    }
                })
            lisDialogBinding.recyclerViewForReUsingDialog.layoutManager = LinearLayoutManager(requireContext())
            lisDialogBinding.recyclerViewForReUsingDialog.adapter = AccountAdapter
            accountDialog.show()
        }

        binding.buttonSaveTransaction.setOnClickListener {
            getDataFromUserInput()
        }
        return binding.root
    }

    private fun selectTransactiontype(type: String) {
        selectedTransactionType = type
    }

    fun getDataFromUserInput() {
        val transaction = selectedTransactionType
        val category = binding.category.text.toString()
        val account = binding.account.text.toString()
        val amount = binding.amount.text.toString()
        val date = binding.date.text.toString()
        val note = binding.note.text.toString()

        if (transaction != null && category.isNotEmpty() && account.isNotEmpty() && amount != null && date.isNotEmpty() && note.isNotEmpty()) {
                try {
                    val amount = amount.toDouble()

                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val transactionDate = sdf.parse(date)

                    if (transactionDate != null) {
                        val transaction = Transaction(id = 0, transType = transaction, category = category, account = account, note = note, date = transactionDate, amount = amount, false)
                        viewModel.vmInsertTransaction(transaction)
                        Toast.makeText(requireContext(), "Transaction added successfully", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Invalid date", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                } catch (e: ParseException) {
                    Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
}