package com.syrous.expensetracker.home

import com.syrous.expensetracker.model.UserTransaction
import java.util.*

sealed class TransactionHeaderItem {

   data class TransactionHeader(
       val date: Date
   ): TransactionHeaderItem()

    data class TransactionItem(
        val userTransaction: UserTransaction
    ): TransactionHeaderItem()
}