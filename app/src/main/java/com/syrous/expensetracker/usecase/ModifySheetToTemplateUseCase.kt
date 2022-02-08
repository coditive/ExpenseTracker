package com.syrous.expensetracker.usecase

import android.content.Context
import android.util.Log
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class ModifySheetToTemplateUseCase @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val sheetApiRequest: SheetApiRequest,
    @Named("apiKey") private val apiKey: String
) {
    private val TAG = this::class.java.name
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

    suspend fun execute(context: Context): UseCaseResult<Boolean> {
        val createSheetResult = createSummarySheet(context)
        return if (createSheetResult is Success) {
            val addCategoriesResult = addCategoriesToSheet(context)
            if (addCategoriesResult is Success) {
                val addDataValidationResult = addDataValidation()
                if (addDataValidationResult is Success)
                    Success(true)
                else Failure("Data Validation Upload Failed!!!")
            } else Failure("Categories Upload Failed!!!")
        } else Failure("Summary sheet creation failed!!!")
    }

    private suspend fun createSummarySheet(context: Context): UseCaseResult<SpreadsheetAppendResponse> {
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
            apiKey,
            Constants.overwrite,
            null,
            Constants.formattedValue,
            Constants.userEntered,
            ValuesRequest(values)
        )

        return if (summaryResult.isSuccessful) {
            values.clear()
            values.add(listOf(sumIfFormula))
            values.add(listOf(sumIfFormula))

            val formulaResult = sheetApiRequest.appendValueIntoSheet(
                sharedPrefManager.getUserToken(),
                sharedPrefManager.getSpreadSheetId(),
                summaryBColumnRange,
                apiKey,
                Constants.overwrite,
                null,
                Constants.formula,
                Constants.userEntered,
                ValuesRequest(values)
            )

            if (formulaResult.isSuccessful) {
                Log.i(TAG, "formula upload Success!!!")
                Success(formulaResult.body()!!)
            } else Failure(formulaResult.message())
        } else Failure(summaryResult.message())
    }

    private suspend fun addCategoriesToSheet(context: Context): UseCaseResult<SpreadsheetAppendResponse> {
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
            apiKey,
            Constants.overwrite,
            null,
            Constants.unformattedValue,
            Constants.userEntered,
            ValuesRequest(values)
        )

        return if (categoriesResult.isSuccessful) {
            if (categoriesResult.body() != null) {
                Log.i(TAG, "category added Success!!!")
                Success(categoriesResult.body()!!)
            } else Failure(categoriesResult.errorBody().toString())
        } else Failure(categoriesResult.message())
    }

    private suspend fun addDataValidation(): UseCaseResult<SpreadSheetBatchUpdateResponse> {
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
            apiKey,
            request
        )

        return if (validationResult.isSuccessful) {
            Log.i(TAG, "data validation upload Success!!!")
            Success(validationResult.body()!!)
        } else
            Failure(validationResult.message())
    }
}