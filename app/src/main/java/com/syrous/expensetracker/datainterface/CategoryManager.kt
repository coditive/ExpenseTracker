package com.syrous.expensetracker.datainterface

interface CategoryManager {

    fun getExpenseCategories(): Array<String>

    fun getIncomeCategories(): Array<String>
}