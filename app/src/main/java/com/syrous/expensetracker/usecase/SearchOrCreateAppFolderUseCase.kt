package com.syrous.expensetracker.usecase

import android.util.Log
import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.model.CreateFolderRequest
import com.syrous.expensetracker.data.remote.model.SearchFileQueryResponse
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response


class SearchOrCreateAppFolderUseCase constructor(
    private val apiRequest: DriveApiRequest,
    private val sharedPrefManager: SharedPrefManager
) {

    suspend fun execute() {
        val query = "mimeType = 'application/vnd.google-apps.folder' and name = 'Expense-Tracker'"

        flow {
            emit(
                apiRequest.searchFile(
                    sharedPrefManager.getUserToken(),
                    Constants.apiKey,
                    Constants.corpora,
                    query
                )
            )
        }.collect { result ->
            if (result.files.isEmpty()) {
                val folder = apiRequest.createFolder(
                    sharedPrefManager.getUserToken(),
                    Constants.apiKey,
                    CreateFolderRequest(
                        Constants.appName,
                        Constants.folderMimeType
                    )
                )

                sharedPrefManager.storeExpenseTrackerFolderId(folder.id)
            } else {
                result.files[0]
                    .let { sharedPrefManager.storeExpenseTrackerFolderId(it.id) }

            }
        }
    }
}

