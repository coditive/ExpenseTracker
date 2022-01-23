package com.syrous.expensetracker.data.remote.model


data class SpreadSheetUpdateRequest(
    val updateSheetProperties: SpreadSheetPropertiesUpdateRequest? = null,
    val setDataValidation: SetDataValidationRequest? = null,
    val addSheet: AddSheet? = null
)


