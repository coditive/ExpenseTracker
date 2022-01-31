package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import javax.inject.Inject

class ModifySheetToTemplateUseCase @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val sheetApiRequest: SheetApiRequest
) {

    suspend fun execute(context: Context) {
        createSummarySheet(context)
        addCategoriesToSheet(context)
        addDataValidation()
    }

    private suspend fun createSummarySheet(context: Context) {
        val expenseCategoryList = context.resources.getStringArray(R.array.expense_categories)
        val values = mutableListOf<List<String>>()
        values.add(listOf("Time Period"))
        values.add(listOf("Earned"))
        values.add(listOf("Spent"))

        for (category in expenseCategoryList) {
            values.add(
                listOf(
                    category,
                    "",
                    "=COUNTIF(Transactions!\$F\$1:D1000,INDIRECT(CONCAT(\"A\",ROW()) ))"
                )
            )
        }

        sheetApiRequest.appendValueIntoSheet(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            "Summary!A:A",
            Constants.apiKey,
            "OVERWRITE",
            "SERIAL_NUMBER",
            "FORMATTED_VALUE",
            "USER_ENTERED",
            ValuesRequest(values)
        )

        values.clear()
        values.add(listOf("=SUMIF(Transactions!E1:E1000,\"INCOME\", Transactions!D1:D1000)"))
        values.add(listOf("=SUMIF(Transactions!E1:E1000,\"EXPENSE\", Transactions!D1:D1000)"))

        sheetApiRequest.appendValueIntoSheet(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            "Summary!B2:B",
            Constants.apiKey,
            "OVERWRITE",
            "SERIAL_NUMBER",
            "FORMULA",
            "USER_ENTERED",
            ValuesRequest(values)
        )

    }

    private suspend fun addCategoriesToSheet(context: Context) {
        val incomeCategoryList = context.resources.getStringArray(R.array.income_categories)
        val expenseCategoryList = context.resources.getStringArray(R.array.expense_categories)
        val values = mutableListOf<List<String>>()
        for (category in incomeCategoryList) {
            values.add(listOf(category))
        }

        for (category in expenseCategoryList) {
            values.add(listOf(category))
        }

        sheetApiRequest.appendValueIntoSheet(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            "Categories!A:A",
            Constants.apiKey,
            "OVERWRITE",
            "SERIAL_NUMBER",
            "UNFORMATTED_VALUE",
            "USER_ENTERED",
            ValuesRequest(values)
        )
    }

    private suspend fun addDataValidation() {
        val dataValidation = SetDataValidationRequest(
            range = GridRange(
                sheetId = sharedPrefManager.getTransactionSheetId(),
                startRowIndex = 1,
                startColumnIndex = 5,
                endColumnIndex = 6
            ),
            rule = DataValidationRule(
                condition = BooleanCondition(
                    type = "ONE_OF_RANGE",
                    values = listOf(
                        ConditionValue(
                            userEnteredValue = "=Categories!\$A\$1:\$A\$1000"
                        )
                    )
                ),
                strict = false,
                showCustomUi = true
            )
        )

        val updateDataValidation = SpreadSheetUpdateRequest(
            setDataValidation = dataValidation
        )

        val request = SpreadSheetBatchUpdateRequest(
            requests = listOf(updateDataValidation)
        )

        sheetApiRequest.updateSpreadSheetToFormat(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            Constants.apiKey,
            request
        )
    }
}