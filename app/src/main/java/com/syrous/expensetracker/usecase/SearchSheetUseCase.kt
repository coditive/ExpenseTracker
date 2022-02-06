package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.model.CreateFolderRequest
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import javax.inject.Inject

class SearchSheetUseCase @Inject constructor(
    private val apiRequest: DriveApiRequest,
    private val sharedPrefManager: SharedPrefManager
) {

    suspend fun execute(): UseCaseResult {
        val query =
            "mimeType = '${Constants.spreadSheetMimeType}' and name = '${Constants.spreadsheetFileName}'"
        val result = apiRequest.searchFile(
            sharedPrefManager.getUserToken(),
            Constants.apiKey,
            Constants.corpora,
            query
        )

        return if (result.isSuccessful) {
            if (result.body() != null) {
                if (result.body()!!.files.isNotEmpty()) {
                    sharedPrefManager.storeFileUploadStatus(true)
                } else {
                    sharedPrefManager.storeSpreadSheetId(result.body()!!.files[0].id)
                }
                Success(true)
            } else Failure(result.errorBody().toString())
        } else
            Failure(result.message())
    }
}