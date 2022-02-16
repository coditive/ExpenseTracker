package com.syrous.expensetracker.utils


import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.data.local.model.SubCategory
import com.syrous.expensetracker.home.TransactionHeaderItem.TransactionHeader
import com.syrous.expensetracker.home.TransactionHeaderItem.TransactionItem
import com.syrous.expensetracker.model.SubCategoryItem
import com.syrous.expensetracker.model.UserTransaction


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

fun SubCategory.toSubCategoryItem(amount: Int, animRes: Int?) = SubCategoryItem(
    itemId = id,
    itemName = name,
    amount,
    animRes
)

private fun String.rupeeFormat(): String {
    var value = this
    value = value.replace(",", "")
    val lastDigit = value[value.length - 1]
    var result = ""
    val len = value.length - 1
    var nDigits = 0
    for (i in len - 1 downTo 0) {
        result = value[i].toString() + result
        nDigits++
        if (nDigits % 2 == 0 && i > 0) {
            result = ",$result"
        }
    }
    return result + lastDigit
}


fun String.getFormattedString(): String =
    if(this.length > 5) {
        val value = this.toInt()
        val amount = value/100000f
        String.format("%.1fL", amount)
    } else this.rupeeFormat()

