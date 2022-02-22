package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.data.remote.model.SpreadsheetAppendResponse
import com.syrous.expensetracker.data.remote.model.ValuesRequest
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.GoogleApisClientProvider
import com.syrous.expensetracker.utils.SharedPrefManager
import javax.inject.Inject
import javax.inject.Named

class AddCategoriesToSheetUseCase @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val provider: GoogleApisClientProvider,
    @Named("apiKey") private val apiKey: String
) {

    private val TAG = this::class.java.name
    private val categoriesAColumnRange = "Categories!A:A"

    suspend fun execute(context: Context): UseCaseResult<SpreadsheetAppendResponse> {
        val incomeCategoryList = context.resources.getStringArray(R.array.income_categories)
        val expenseCategoryList = context.resources.getStringArray(R.array.expense_categories)

        return if (incomeCategoryList.isNotEmpty() && expenseCategoryList.isNotEmpty()) {
            val values = mutableListOf<List<String>>()
            for (category in incomeCategoryList) {
                values.add(listOf(category))
            }

            for (category in expenseCategoryList) {
                values.add(listOf(category))
            }

            val categoriesResult = provider.sheetApiClient().appendValueIntoSheet(
                sharedPrefManager.getSpreadSheetId(),
                categoriesAColumnRange,
                apiKey,
                Constants.overwrite,
                null,
                Constants.unformattedValue,
                Constants.userEntered,
                ValuesRequest(values)
            )

            if (categoriesResult.isSuccessful) {
                if (categoriesResult.body() != null) {
//                    Log.i(TAG, "category added Success!!!")
                    Success(categoriesResult.body()!!)
                } else Failure(categoriesResult.errorBody().toString())
            } else Failure(categoriesResult.message())
        } else {
            if (expenseCategoryList.isEmpty()) Failure("Expense Categories are empty")
            else Failure("Income categories are empty")
        }
    }
}