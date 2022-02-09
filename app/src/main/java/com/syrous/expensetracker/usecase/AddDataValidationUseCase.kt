package com.syrous.expensetracker.usecase

import android.util.Log
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.SharedPrefManager
import javax.inject.Inject
import javax.inject.Named

class AddDataValidationUseCase @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val sheetApiRequest: SheetApiRequest,
    @Named("apiKey") private val apiKey: String
) {
    private val categoryFormula = "=Categories!\$A\$1:\$A\$1000"
    private val oneOfRange = "ONE_OF_RANGE"
    private val TAG = this::class.java.name

    suspend fun execute(): UseCaseResult<SpreadSheetBatchUpdateResponse> {
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
//            Log.i(TAG, "data validation upload Success!!!")
            Success(validationResult.body()!!)
        } else
            Failure(validationResult.message())
    }
}