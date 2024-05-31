package com.example.paisefy_moneymanager.utils

import java.text.SimpleDateFormat
import java.util.Date

class Helper {
    companion object {
        fun formateDate(date: Date): String {
            val simpleDateformat = SimpleDateFormat("dd MMMM yyyy")
            return simpleDateformat.format(date)
        }
    }
}