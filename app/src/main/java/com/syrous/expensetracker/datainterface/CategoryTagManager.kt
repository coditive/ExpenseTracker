package com.syrous.expensetracker.datainterface

import android.content.Context
import com.syrous.expensetracker.R

interface CategoryManager {

    fun getStoredExpenseCategories(): Array<String>

    fun getStoredIncomeCategories(): Array<String>

}

class CategoryManagerImpl(val context: Context): CategoryManager {

    override fun getStoredExpenseCategories(): Array<String> {
        return context.resources.getStringArray(R.array.expense_categories)
    }

    override fun getStoredIncomeCategories(): Array<String>{
        return context.resources.getStringArray(R.array.income_categories)
    }




}