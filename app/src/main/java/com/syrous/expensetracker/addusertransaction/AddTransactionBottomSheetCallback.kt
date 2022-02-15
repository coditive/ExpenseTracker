package com.syrous.expensetracker.addusertransaction

import com.syrous.expensetracker.data.local.model.SubCategory
import com.syrous.expensetracker.model.SubCategoryItem

interface AddTransactionBottomSheetCallback {

    fun amountNextButtonClicked(amount: Int)

    fun descriptionSaveButtonClicked(description: String)

    fun calendarIconClicked(callback: CalendarDismissCallback)

    fun subCategoryClicked(subCategory: SubCategoryItem)
}