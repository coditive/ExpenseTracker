package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import kotlin.math.abs
import kotlin.random.Random

class CreateSheetsUseCase constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val sheetApiRequest: SheetApiRequest
) {

    suspend fun execute() {
        flow {
            emit(
                sheetApiRequest.getSpreadSheetData(
                    sharedPrefManager.getUserToken(),
                    sharedPrefManager.getSpreadSheetId(),
                    Constants.apiKey
                )
            )
        }.collect { response ->
            if (response.isSuccessful) {
                sharedPrefManager.storeTransactionSheetId(response.body()?.sheets!![0].properties.sheetId)

                val spreadsheetProperties = SheetProperties(
                    sheetId = sharedPrefManager.getTransactionSheetId(),
                    title = "Transactions",
                    index = 1,
                    sheetType = Constants.gridSheetType
                )

                val spreadSheetPropertiesUpdate = SpreadSheetPropertiesUpdateRequest(
                    properties = spreadsheetProperties,
                    "title"
                )

                sharedPrefManager.storeSummarySheetId(abs(Random.nextInt()))

                val addSummarySheetRequest = AddSheet(
                    SheetProperties(
                        sheetId = sharedPrefManager.getSummarySheetId(),
                        index = 0,
                        sheetType = Constants.gridSheetType,
                        title = "Summary",
                        gridProperties = null
                    )
                )

                sharedPrefManager.storeCategoriesSheetId(abs(Random.nextInt()))

                val addCategoriesSheetRequest = AddSheet(
                    SheetProperties(
                        sheetId = sharedPrefManager.getCategoriesSheetId(),
                        index = 2,
                        sheetType = Constants.gridSheetType,
                        title = "Categories",
                        gridProperties = null
                    )
                )

                val batchUpdateRequest = SpreadSheetBatchUpdateRequest(
                    requests = listOf(
                        SpreadSheetUpdateRequest(addSheet = addSummarySheetRequest),
                        SpreadSheetUpdateRequest(addSheet = addCategoriesSheetRequest),
                        SpreadSheetUpdateRequest(updateSheetProperties = spreadSheetPropertiesUpdate)
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
    }
}