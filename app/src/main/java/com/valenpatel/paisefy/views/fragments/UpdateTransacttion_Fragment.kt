package com.valenpatel.paisefy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.valenpatel.paisefy.R


class UpdateTransacttion_Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_transacttion, container, false)

        //NOT USING THIS FRAGMENT
        // ADDING AND UPADTE ARE DONE ON SAME FRAGMENT
    }
}