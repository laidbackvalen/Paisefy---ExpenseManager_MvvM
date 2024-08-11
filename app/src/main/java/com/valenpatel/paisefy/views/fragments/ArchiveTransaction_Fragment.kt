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
import com.valenpatel.paisefy.adapter.ArchiveTransactionAdapter
import com.valenpatel.paisefy.databinding.FragmentArchiveTransactionBinding
import com.valenpatel.paisefy.db.entities.ArchivedTransaction
import com.valenpatel.paisefy.viewmodel.MainViewModel


class ArchiveTransaction_Fragment : Fragment() {

    lateinit var binding: FragmentArchiveTransactionBinding
    lateinit var archiveTransactionAdapter: ArchiveTransactionAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentArchiveTransactionBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.archiveList.observe(viewLifecycleOwner, Observer { data ->
            data?.let {
                archiveTransactionAdapter = ArchiveTransactionAdapter(requireContext(), it as ArrayList<ArchivedTransaction>)
                val layoutManager = LinearLayoutManager(context)
                binding.recyclerViewArchive.layoutManager = layoutManager
                binding.recyclerViewArchive.adapter = archiveTransactionAdapter
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