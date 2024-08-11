package com.valenpatel.paisefy.utils

import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.model.Category


class Constants {
    companion object {
        const val INCOME = "INCOME"
        const val EXPENSE = "EXPENSE"

        val categoryArrayList: List<Category> by lazy {
            listOf(
                Category("Bills", R.drawable.bill_receipt_svgrepo_com, R.color.c1),
                Category("Business", R.drawable.business, R.color.c2),
                Category("Childcare", R.drawable.pram_pushchair_buggy_baby_childcare_svgrepo_com, R.color.c3),
                Category("Clothing", R.drawable.clothing, R.color.c4),
                Category("Dining", R.drawable.dining, R.color.c5),
                Category("Education", R.drawable.education, R.color.c6),
                Category("EMI", R.drawable.calculator, R.color.c7),
                Category("Entertainment", R.drawable.entertainment, R.color.c8),
                Category("Fuel", R.drawable.fuel, R.color.c9),
                Category("Gifts", R.drawable.gift, R.color.c10),
                Category("Groceries", R.drawable.suppliesbasketgroceries, R.color.c11),
                Category("Gadgets", R.drawable.device, R.color.c12),
                Category("Grooming", R.drawable.grooming, R.color.c13),
                Category("Healthcare", R.drawable.healthcare, R.color.c14),
                Category("Household", R.drawable.mixer, R.color.c15),
                Category("Investment", R.drawable.investment, R.color.c16),
                Category("Loan", R.drawable.loan, R.color.c17),
                Category("Leisure", R.drawable.leisure, R.color.c18),
                Category("misc", R.drawable.loan, R.color.c19),
                Category("Office", R.drawable.office_desk_svgrepo_com, R.color.c20),
                Category("Pet Care", R.drawable.veterinary, R.color.c21),
                Category("Rent", R.drawable.rent, R.color.c22),
                Category("Salary", R.drawable.salary, R.color.c23),
                Category("Subscription", R.drawable.subscriptions, R.color.c24),
                Category("Shopping", R.drawable.shopping, R.color.c25),
                Category("Travel", R.drawable.route, R.color.c26),
                Category("Other", R.drawable.others, R.color.c27),
            )
        }

        fun getCategoryDetails(category: String): Category? {
            //the find function is used to search for the first element in a collection that matches a given predicate.
            // It returns the first matching element if found, or null if no such element exists.
            return categoryArrayList.find { it.categoryName == category }
        }
    }
}