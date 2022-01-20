package com.syrous.expensetracker.upload

import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import kotlin.math.abs
import kotlin.random.Random

class CreateSheetsUseCase constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val sheetApiRequest: SheetApiRequest
) {
    
    suspend fun execute() {
        getSpreadSheetDetails()
    }
    
    private suspend fun getSpreadSheetDetails() {
        val spreadSheetId = sharedPrefManager.getSpreadSheetId()
        val spreadSheetResponse = sheetApiRequest.getSpreadSheetData(
            sharedPrefManager.getUserToken(),
            spreadSheetId,
            Constants.apiKey
        )

        val addSummarySheetRequest = AddSheet(
            SheetProperties(
                sheetId = abs(Random.nextInt()),
                index = 0,
                sheetType = Constants.gridSheetType,
                title = "Summary",
                gridProperties = null
            )
        )

        val addCategoriesSheetRequest = AddSheet(
            SheetProperties(
                sheetId = abs(Random.nextInt()),
                index = 2,
                sheetType = Constants.gridSheetType,
                title = "Categories",
                gridProperties = null
            )
        )

        val batchUpdateRequest = SpreadSheetBatchUpdateRequest(
            requests = listOf(
                SpreadSheetUpdateRequest(addSummarySheetRequest),
                SpreadSheetUpdateRequest(addCategoriesSheetRequest)
            )
        )

        sheetApiRequest.updateSpreadSheetToFormat(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            Constants.apiKey,
            batchUpdateRequest
        )

    }
}