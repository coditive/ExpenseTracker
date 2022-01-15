package com.syrous.expensetracker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syrous.expensetracker.data.remote.model.RemoteTransaction
import com.syrous.expensetracker.data.remote.model.RemoteUserTransaction
import java.text.SimpleDateFormat
import java.util.*


@Entity
data class UserTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val description: String,
    val date: Date,
    val transactionCategory: TransactionCategory,
    val categoryTag: String,
)


fun UserTransaction.toRemoteUserTransaction(): RemoteTransaction {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return RemoteTransaction(
        sheet = RemoteUserTransaction(
                id,
                amount,
                description,
                sdf.format(date),
                transactionCategory.name,
                categoryTag
            )

    )
}