package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.usecase.UseCaseResult.*
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random

class CreateSheetsUseCase @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val sheetApiRequest: SheetApiRequest
) {

    suspend fun execute(): UseCaseResult {
        val result = sheetApiRequest.getSpreadSheetData(
            sharedPrefManager.getUserToken(),
            sharedPrefManager.getSpreadSheetId(),
            Constants.apiKey
        )
        return if (result.isSuccessful) {
            val batchUpdateRequest = storeAndCreateAddSheetRequest(result)
            val updateResult = sheetApiRequest.updateSpreadSheetToFormat(
                sharedPrefManager.getUserToken(),
                sharedPrefManager.getSpreadSheetId(),
                Constants.apiKey,
                batchUpdateRequest
            )

            if (updateResult.isSuccessful) {
                Success(true)
            } else {
                Failure(updateResult.message())
            }

        } else {
            Failure(result.message())
        }
    }


    private fun storeAndCreateAddSheetRequest(result: Response<SpreadSheetResponse>): SpreadSheetBatchUpdateRequest {
        sharedPrefManager.storeTransactionSheetId(result.body()?.sheets!![0].properties.sheetId)

        val spreadsheetProperties = SheetProperties(
            sheetId = sharedPrefManager.getTransactionSheetId(),
            title = Constants.transaction,
            index = 1,
            sheetType = Constants.gridSheetType
        )

        val spreadSheetPropertiesUpdate = SpreadSheetPropertiesUpdateRequest(
            properties = spreadsheetProperties,
            Constants.title
        )

        sharedPrefManager.storeSummarySheetId(abs(Random.nextInt()))

        val addSummarySheetRequest = AddSheet(
            SheetProperties(
                sheetId = sharedPrefManager.getSummarySheetId(),
                index = 0,
                sheetType = Constants.gridSheetType,
                title = Constants.summary
            )
        )

        sharedPrefManager.storeCategoriesSheetId(abs(Random.nextInt()))

        val addCategoriesSheetRequest = AddSheet(
            SheetProperties(
                sheetId = sharedPrefManager.getCategoriesSheetId(),
                index = 2,
                sheetType = Constants.gridSheetType,
                title = Constants.categories
            )
        )

        return SpreadSheetBatchUpdateRequest(
            requests = listOf(
                SpreadSheetUpdateRequest(addSheet = addSummarySheetRequest),
                SpreadSheetUpdateRequest(addSheet = addCategoriesSheetRequest),
                SpreadSheetUpdateRequest(updateSheetProperties = spreadSheetPropertiesUpdate)
            )
        )
    }
}