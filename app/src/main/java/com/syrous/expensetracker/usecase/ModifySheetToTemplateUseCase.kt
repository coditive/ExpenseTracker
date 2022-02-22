package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import javax.inject.Inject

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