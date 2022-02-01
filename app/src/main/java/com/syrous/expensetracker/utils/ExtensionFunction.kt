package com.syrous.expensetracker.utils


import com.syrous.expensetracker.data.local.model.DBDashboardSubCategoryItem
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.model.UserTransaction
import java.util.*


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


fun DBDashboardSubCategoryItem.toDashboardCategoryItem(categoryTag: String): DashboardCategoryItem =
    DashboardCategoryItem(
        subCategoryId,
        categoryTag,
        this.totalAmountSpent,
        this.count
    )