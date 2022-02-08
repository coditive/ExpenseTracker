package com.syrous.expensetracker.usecase

import android.util.Log
import androidx.work.ListenableWorker
import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.model.SearchFileQueryResponse
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import javax.inject.Inject
import javax.inject.Named

class SearchSheetUseCase @Inject constructor(
    private val apiRequest: DriveApiRequest,
    private val sharedPrefManager: SharedPrefManager,
    @Named("apiKey") private val apiKey: String
) {
    private val TAG = this::class.java.name
    suspend fun execute(): UseCaseResult<SearchFileQueryResponse> {
        val query =
            "mimeType = '${Constants.spreadSheetMimeType}' and name = '${Constants.spreadsheetFileName}'"
        val result = apiRequest.searchFile(
            sharedPrefManager.getUserToken(),
            apiKey,
            Constants.corpora,
            query
        )

        return if (result.isSuccessful) {
            if (result.body() != null) {
                if (result.body()!!.files.isEmpty()) {
                    sharedPrefManager.storeFileUploadedStatus(false)
                } else {
                    sharedPrefManager.storeSpreadSheetId(result.body()!!.files[0].id)
                    sharedPrefManager.storeFileUploadedStatus(true)
                }
//                Log.i(TAG, "search sheet Success!!!")
                Success(result.body()!!)
            } else Failure(result.errorBody().toString())
        } else
            Failure(result.message())
    }
}