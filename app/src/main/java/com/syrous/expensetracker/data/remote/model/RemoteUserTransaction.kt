package com.syrous.expensetracker.data.remote.model

import androidx.room.PrimaryKey
import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.data.local.model.UserTransaction
import java.text.SimpleDateFormat
import java.util.*

data class RemoteUserTransaction(
    val id: Long = 0,
    val amount: Int,
    val description: String,
    val date: String,
    val transactionCategory: Int,
    val categoryTag: String,
)


fun RemoteUserTransaction.toUserTransaction(): UserTransaction {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return UserTransaction(
        id,
        amount,
        description,
        sdf.parse(date)!!,
        if(transactionCategory == 0) TransactionCategory.INCOME else TransactionCategory.EXPENSE,
        categoryTag
    )
}