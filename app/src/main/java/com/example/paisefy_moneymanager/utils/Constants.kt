package com.example.paisefy_moneymanager.utils

import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.model.Category

class Constants {
    companion object {
        const val INCOME = "INCOME"
        const val EXPENSE = "EXPENSE"

        val categoryArrayList: List<Category> by lazy {
            listOf(
                Category("Salary", R.drawable.salary, R.color.income),
                Category("Business", R.drawable.business, R.color.dark_orange),
                Category("Investment", R.drawable.investment, R.color.category4),
                Category("Loan", R.drawable.loan, R.color.category6),
                Category("Rent", R.drawable.rent, R.color.category5),
                Category("Other", R.drawable.others, R.color.category3)
            )
        }

        fun getCategoryDetails(category: String): Category? {
            //the find function is used to search for the first element in a collection that matches a given predicate.
            // It returns the first matching element if found, or null if no such element exists.
            return categoryArrayList.find { it.categoryName == category }
        }
    }

}