package com.valenpatel.paisefy.views.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.adapter.AccountsAdapter
import com.valenpatel.paisefy.adapter.CategoryAdapter
import com.valenpatel.paisefy.databinding.FragmentAddTranscationBinding
import com.valenpatel.paisefy.databinding.ListDialogBinding
import com.valenpatel.paisefy.model.Account
import com.valenpatel.paisefy.model.Category
import com.valenpatel.paisefy.model.Transaction
import com.valenpatel.paisefy.utils.Constants
import com.valenpatel.paisefy.utils.Helper
import com.valenpatel.paisefy.viewmodel.MainViewModel
import com.valenpatel.paisefy.views.activities.MainActivity
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.ParseException
import java.util.Locale

class Add_Transcation_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTranscationBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var selectedTransactionType: String? = null
    private var bitmap: Bitmap? = null
    private var transactionId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTranscationBinding.inflate(inflater, container, false)

        setupTransactionTypeButtons()
        setupDatePicker()
        setupCategorySelector()
        setupAccountSelector()
        setupImagePicker()
        setupSaveButton()

        // Handle editing an existing transaction
        arguments?.getParcelable<Transaction>("transaction")?.let { transaction ->
            populateFieldsForEditing(transaction)
        }

        return binding.root
    }

    private fun setupTransactionTypeButtons() {
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
    }

    private fun setupDatePicker() {
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
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
        val date = simpleDateFormat.format(calendar.time)
        binding.date.setText(date)
    }

    private fun setupCategorySelector() {
        binding.category.setOnClickListener {
            val lisDialogBinding = ListDialogBinding.inflate(layoutInflater)
            val categoryDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
            categoryDialog.setView(lisDialogBinding.root)

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
    }

    private fun setupAccountSelector() {
        binding.account.setOnClickListener {
            val lisDialogBinding = ListDialogBinding.inflate(layoutInflater)
            val accountDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
            accountDialog.setView(lisDialogBinding.root)

            val accountArrayList = arrayListOf(
                Account(0.0, "Cash"),
                Account(0.0, "BANK / NEFT / RTGS / CHEQUE / DRAFT"),
                Account(0.0, "UPI"),
                Account(0.0, "Other")
            )

            val accountAdapter = AccountsAdapter(requireContext(), accountArrayList, object : AccountsAdapter.AccountTypeClickListener {
                override fun onAccountTypeClick(accountType: String) {
                    binding.account.setText(accountType)
                    accountDialog.dismiss()
                }
            })
            lisDialogBinding.recyclerViewForReUsingDialog.layoutManager = LinearLayoutManager(requireContext())
            lisDialogBinding.recyclerViewForReUsingDialog.adapter = accountAdapter
            accountDialog.show()
        }
    }

    private fun setupImagePicker() {
        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(it)
                bitmap = BitmapFactory.decodeStream(inputStream)
                binding.imageView14.setImageBitmap(bitmap)
            }
        }

        binding.imageView14.setOnClickListener {
            activityResultLauncher.launch("image/*")
        }
    }

    private fun setupSaveButton() {
        binding.buttonSaveTransaction.setOnClickListener {
            if (transactionId == null) {
                getDataFromUserInput()
            } else {
                updateTransaction()
            }
        }
    }

    private fun selectTransactiontype(type: String) {
        selectedTransactionType = type
    }

    private fun populateFieldsForEditing(transaction: Transaction) {
        binding.date.setText(Helper.formateDate(transaction.date))
        binding.category.setText(transaction.category)
        binding.amount.setText(transaction.amount.toString())
        binding.note.setText(transaction.note)
        binding.account.setText(transaction.account)

        Glide.with(this)
            .load(transaction.image)
            .fitCenter()
            .placeholder(R.drawable.baseline_image_24)
            .into(binding.imageView14)

        transactionId = transaction.id
        selectedTransactionType = transaction.transType
        if (transaction.transType == Constants.EXPENSE) {
            binding.expenseButton.setTextColor(Color.WHITE)
            binding.expenseButton.setBackgroundResource(R.drawable.bg_expense)
            binding.incomeButton.setTextColor(resources.getColor(R.color.income))
            binding.incomeButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_income)
        } else if (transaction.transType == Constants.INCOME) {
            binding.incomeButton.setTextColor(Color.WHITE)
            binding.incomeButton.setBackgroundResource(R.drawable.bg_income)
            binding.expenseButton.setTextColor(resources.getColor(R.color.red))
            binding.expenseButton.setBackgroundResource(R.drawable.txtbottomsheet_bg_expense)
        }
    }

    private fun getDataFromUserInput() {
        val transaction = selectedTransactionType
        val category = binding.category.text.toString()
        val account = binding.account.text.toString()
        val amount = binding.amount.text.toString()
        val date = binding.date.text.toString()
        val note = binding.note.text.toString()

        if (transaction != null && category.isNotEmpty() && account.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty() && note.isNotEmpty()) {
            try {
                val amount = amount.toDouble()
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val transactionDate = sdf.parse(date)

                if (transactionDate != null) {
                    val transaction = Transaction(
                        id = 0,  // This will be set correctly in the update function
                        transType = transaction,
                        category = category,
                        account = account,
                        note = note,
                        date = transactionDate,
                        amount = amount,
                        false,
                        bitmap?.let { convertBitmapToByteArray(it) }
                    )
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

    private fun updateTransaction() {
        val transaction = selectedTransactionType
        val category = binding.category.text.toString()
        val account = binding.account.text.toString()
        val amount = binding.amount.text.toString()
        val date = binding.date.text.toString()
        val note = binding.note.text.toString()

        if (transaction != null && category.isNotEmpty() && account.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty() && note.isNotEmpty()) {
            try {
                val amount = amount.toDouble()
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val transactionDate = sdf.parse(date)

                if (transactionDate != null && transactionId != null) {
                    val updatedTransaction = Transaction(
                        id = transactionId!!,
                        transType = transaction,
                        category = category,
                        account = account,
                        note = note,
                        date = transactionDate,
                        amount = amount,
                        false,
                        bitmap?.let { convertBitmapToByteArray(it) }
                    )
                    viewModel.vmUpdateTransaction(updatedTransaction)
                    Toast.makeText(requireContext(), "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).getFragment(TransactionFragment())
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

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        compressAndResizeBitmap(bitmap).compress(Bitmap.CompressFormat.PNG, 50, stream)
        return stream.toByteArray()
    }

    private fun compressAndResizeBitmap(bitmap: Bitmap): Bitmap {
        val maxSize = 1024 // Maximum size in pixels (width/height)
        var width = bitmap.width
        var height = bitmap.height

        if (width > maxSize || height > maxSize) {
            val aspectRatio: Float = width.toFloat() / height.toFloat()
            if (aspectRatio > 1) {
                width = maxSize
                height = (maxSize / aspectRatio).toInt()
            } else {
                height = maxSize
                width = (maxSize * aspectRatio).toInt()
            }
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
