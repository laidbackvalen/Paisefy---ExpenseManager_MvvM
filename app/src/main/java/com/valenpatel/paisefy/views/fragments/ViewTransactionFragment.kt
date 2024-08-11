package com.valenpatel.paisefy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.databinding.FragmentViewTransactionBinding
import com.valenpatel.paisefy.model.Transaction
import com.valenpatel.paisefy.utils.Constants
import com.valenpatel.paisefy.utils.Helper
import com.valenpatel.paisefy.views.activities.MainActivity


class ViewTransactionFragment : Fragment() {
    lateinit var binding: FragmentViewTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewTransactionBinding.inflate(layoutInflater)
        arguments?.getParcelable<Transaction>("transaction")?.let { transaction ->
            // Use the transaction data to update the UI elements
            binding.transactionDate.text = Helper.formateDate(transaction.date)
            binding.transactionType.text = transaction.transType.toString()
            binding.transactionCategory.text = transaction.category
            binding.transactionAmount.text = "₹ "+transaction.amount.toString()
            binding.transactionNote.text = transaction.note
            binding.transactionAccount.text = transaction.account.toString()

            if (transaction.transType.equals(Constants.EXPENSE)) {
                binding.transactionType.setTextColor(requireContext().getColor(R.color.red))
            } else if (transaction.transType.equals(Constants.INCOME)) {
               binding.transactionType.setTextColor(requireContext().getColor(R.color.income))
            }
            Glide
                .with(this)
                .load(transaction.image)
                .fitCenter()
                .placeholder(R.drawable.baseline_image_24)
                .into(binding.viewTransactionImg);

            // Set click listener on the edit button
            binding.updateTransaction.setOnClickListener {
                navigateToUpdateTransaction(transaction)
            }

        }
        // Handle back press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack()
                } else {
                    requireActivity().onBackPressed()
                }
            }
        })

        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }
    private fun navigateToUpdateTransaction(transaction: Transaction) {
        // Create a new instance of UpdateTransactionFragment
        val updateTransactionFragment = Add_Transcation_Fragment()

        // Create a bundle to hold the transaction data
        val bundle = Bundle()
        bundle.putParcelable("transaction", transaction)

        // Set the bundle as the arguments for the UpdateTransactionFragment
        updateTransactionFragment.arguments = bundle

        // Replace the current fragment with the Add_Transcation_Fragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameContentUnderMain, updateTransactionFragment) // Use your fragment container ID
            .addToBackStack(null) // Add the transaction to the back stack to allow navigation back
            .commit()
    }
}
