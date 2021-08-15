package com.syrous.expensetracker.datainterface

import com.syrous.expensetracker.model.Transaction

interface TransactionManager {

    fun addTransactionToStorage(transaction: Transaction)

    fun getTransactionsFromStorage(transactionCategory: Int): List<Transaction>
}