package com.valenpatel.paisefy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.valenpatel.paisefy.adapter.RecentlyDeletedAdapter
import com.valenpatel.paisefy.databinding.FragmentRecentlyDeletedTransactionBinding
import com.valenpatel.paisefy.db.entities.RecentlyDeletedDataEntity
import com.valenpatel.paisefy.viewmodel.MainViewModel


class RecentlyDeletedTransaction_Fragment : Fragment() {

    lateinit var binding: FragmentRecentlyDeletedTransactionBinding
    lateinit var recentlyDeletedTransactionAdapter: RecentlyDeletedAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecentlyDeletedTransactionBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.recentlyDeletedTransactions.observe(viewLifecycleOwner, Observer { data ->
            data?.let {
                recentlyDeletedTransactionAdapter = RecentlyDeletedAdapter(requireContext(), it as ArrayList<RecentlyDeletedDataEntity>)
                val layoutManager = LinearLayoutManager(context)
                binding.recyclerViewRecent.layoutManager = layoutManager
                binding.recyclerViewRecent.adapter = recentlyDeletedTransactionAdapter
            }
        })

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

        binding.backimg.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }


}