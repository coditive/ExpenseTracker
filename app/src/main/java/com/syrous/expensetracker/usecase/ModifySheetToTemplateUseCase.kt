package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import retrofit2.Response
import javax.inject.Inject

class ModifySheetToTemplateUseCase @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val sheetApiRequest: SheetApiRequest
) {

    private val timePeriod = "Time Period"

    private val earned = "Earned"

    private val spent = "Spent"

    private val countIfFormula =
        "=COUNTIF(Transactions!\$F\$1:D1000,INDIRECT(CONCAT(\"A\",ROW()) ))"

    private val sumIfFormula = "=SUMIF(Transactions!E1:E1000,\"INCOME\", Transactions!D1:D1000)"

    private val categoryFormula = "=Categories!\$A\$1:\$A\$1000"

    private val oneOfRange = "ONE_OF_RANGE"

    private val summaryAColumnRange = "Summary!A:A"

    private val summaryBColumnRange = "Summary!B2:B"

    private val categoriesAColumnRange = "Categories!A:A"

    suspend fun execute(context: Context): UseCaseResult {
        val createSheetResult = createSummarySheet(context)
        return if (createSheetResult is Success) {
            val addCategoriesResult = addCategoriesToSheet(context)
            if (addCategoriesResult is Success)
                addDataValidation()
            else addCategoriesResult
        } else createSheetResult
    }

    private suspend fun createSummarySheet(context: Context): UseCaseResult {
        val expenseCategoryList = context.resources.getStringArray(R.array.expense_categories)
        val values = mutableListOf<List<String>>()
        values.add(listOf(timePeriod))
        values.add(listOf(earned))
        values.add(listOf(spent))

        for (category in expenseCategoryList) {
            values.add(
                listOf(
                    category,
                    Constants.emptyString,
                    countIfFormula
                )
            )
        }

        val summaryResult = sheetApiRequest.appendValueIntoSheet(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            summaryAColumnRange,
            Constants.apiKey,
            Constants.overwrite,
            null,
            Constants.formattedValue,
            Constants.userEntered,
            ValuesRequest(values)
        )

        if (summaryResult.isSuccessful) {
            values.clear()
            values.add(listOf(sumIfFormula))
            values.add(listOf(sumIfFormula))

            val formulaResult = sheetApiRequest.appendValueIntoSheet(
                sharedPrefManager.getUserToken(),
                sharedPrefManager.getSpreadSheetId(),
                summaryBColumnRange,
                Constants.apiKey,
                Constants.overwrite,
                null,
                Constants.formula,
                Constants.userEntered,
                ValuesRequest(values)
            )

            return if (formulaResult.isSuccessful)
                Success(true)
            else
                Failure(formulaResult.message())
        } else
            return Failure(summaryResult.message())
    }

    private suspend fun addCategoriesToSheet(context: Context): UseCaseResult {
        val incomeCategoryList = context.resources.getStringArray(R.array.income_categories)
        val expenseCategoryList = context.resources.getStringArray(R.array.expense_categories)
        val values = mutableListOf<List<String>>()
        for (category in incomeCategoryList) {
            values.add(listOf(category))
        }

        for (category in expenseCategoryList) {
            values.add(listOf(category))
        }

        val categoriesResult = sheetApiRequest.appendValueIntoSheet(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            categoriesAColumnRange,
            Constants.apiKey,
            Constants.overwrite,
            null,
            Constants.unformattedValue,
            Constants.userEntered,
            ValuesRequest(values)
        )

        return if (categoriesResult.isSuccessful)
            Success(true)
        else
            Failure(categoriesResult.message())
    }

    private suspend fun addDataValidation(): UseCaseResult {
        val dataValidation = SetDataValidationRequest(
            range = GridRange(
                sheetId = sharedPrefManager.getTransactionSheetId(),
                startRowIndex = 1,
                startColumnIndex = 5,
                endColumnIndex = 6
            ),
            rule = DataValidationRule(
                condition = BooleanCondition(
                    type = oneOfRange,
                    values = listOf(
                        ConditionValue(
                            userEnteredValue = categoryFormula
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

        val validationResult = sheetApiRequest.updateSpreadSheetToFormat(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            Constants.apiKey,
            request
        )

        return if (validationResult.isSuccessful)
            Success(true)
        else
            Failure(validationResult.message())
    }
}