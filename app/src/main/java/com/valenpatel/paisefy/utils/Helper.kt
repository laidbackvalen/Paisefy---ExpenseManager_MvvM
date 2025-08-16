package com.valenpatel.paisefy.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Helper {
    companion object {
//        fun formateDate(date: Date): String {  // change Long to date
//            val simpleDateformat = SimpleDateFormat("dd MMMM yyyy")
//            return simpleDateformat.format(date)
//        }
        fun formateDate(date: Any): String {   //Any input
            return when (date) {
                is Date -> { //if date
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    sdf.format(date)
                }
                is Long -> { // if long
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    sdf.format(Date(date))
                }
                else -> {
                    throw IllegalArgumentException("Unsupported date type: ${date.javaClass}")
                }
            }
        }

        fun formateDateByMonth(date: Date): String {
            val simpleDateformat = SimpleDateFormat("MMMM yyyy")   // change date to this format in month
            return simpleDateformat.format(date)
        }
    }
}