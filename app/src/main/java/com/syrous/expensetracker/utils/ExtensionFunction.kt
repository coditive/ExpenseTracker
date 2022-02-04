package com.syrous.expensetracker.utils


import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.screen.viewtransaction.TransactionHeaderItem
import com.syrous.expensetracker.screen.viewtransaction.TransactionHeaderItem.TransactionHeader
import com.syrous.expensetracker.screen.viewtransaction.TransactionHeaderItem.TransactionItem


fun UserTransaction.toDBTransaction(categoryId: Int): DBTransaction = DBTransaction(
    this.id,
    this.amount,
    this.description,
    this.category,
    this.date,
    categoryId,
    false
)

fun DBTransaction.toUserTransaction(categoryTag: String): UserTransaction = UserTransaction(
    this.timestamp,
    this.amount,
    this.description,
    this.date,
    this.category,
    categoryTag
)

fun UserTransaction.toTransactionItem(): TransactionItem = TransactionItem(
    userTransaction = this
)

fun UserTransaction.toTransactionHeader(): TransactionHeader = TransactionHeader(
    date = this.date
)
