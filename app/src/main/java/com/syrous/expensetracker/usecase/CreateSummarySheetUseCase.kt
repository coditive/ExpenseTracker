package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.SpreadsheetAppendResponse
import com.syrous.expensetracker.data.remote.model.ValuesRequest
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import javax.inject.Inject
import javax.inject.Named

class CreateSummarySheetUseCase @Inject constructor(
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

    private val summaryAColumnRange = "Summary!A:A"

    private val summaryBColumnRange = "Summary!B2:B"

    @Throws(Exception::class)
    suspend fun execute(context: Context): UseCaseResult<SpreadsheetAppendResponse> {
        val expenseCategoryList = context.resources.getStringArray(R.array.expense_categories)
        val values = mutableListOf<List<String>>()
        values.add(listOf(timePeriod))
        values.add(listOf(earned))
        values.add(listOf(spent))

        return if (expenseCategoryList.isNotEmpty()) {
            for (category in expenseCategoryList) {
                values.add(
                    listOf(
                        category,
                        Constants.emptyString,
                        countIfFormula
                    )
                )
            }

            try {
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

                if (summaryResult.isSuccessful) {
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
//                        Log.i(TAG, "formula upload Success!!!")
                        UseCaseResult.Success(formulaResult.body()!!)
                    } else UseCaseResult.Failure(formulaResult.message())
                } else UseCaseResult.Failure(summaryResult.message())
            } catch (e: Exception) {
                throw e
            }
        } else UseCaseResult.Failure("Category list is empty")
    }
}