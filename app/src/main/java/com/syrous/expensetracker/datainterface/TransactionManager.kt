package com.syrous.expensetracker.datainterface

import com.syrous.expensetracker.model.Transaction

interface TransactionManager {

    fun addTransactionToStorage(transaction: Transaction)

    fun getAllTransactionsFromStorage(): List<Transaction>

    fun getCategorisedTransactionsFromStorage(transactionCategory: Int): List<Transaction>

    fun getTotalExpenses(): Int

    fun getTotalIncome(): Int
}