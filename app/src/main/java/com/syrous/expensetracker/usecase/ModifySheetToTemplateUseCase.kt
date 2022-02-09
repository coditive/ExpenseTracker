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
    private val createSummarySheet: CreateSummarySheetUseCase,
    private val addCategoriesToSheet: AddCategoriesToSheetUseCase,
    private val addDataValidation: AddDataValidationUseCase
) {
    suspend fun execute(context: Context): UseCaseResult<Boolean> {
        val createSheetResult = createSummarySheet.execute(context)
        return if (createSheetResult is Success) {
            val addCategoriesResult = addCategoriesToSheet.execute(context)
            if (addCategoriesResult is Success) {
                val addDataValidationResult = addDataValidation.execute()
                if (addDataValidationResult is Success)
                    Success(true)
                else Failure("Data Validation Upload Failed!!!")
            } else Failure("Categories Upload Failed!!!")
        } else Failure("Summary sheet creation failed!!!")
    }
}