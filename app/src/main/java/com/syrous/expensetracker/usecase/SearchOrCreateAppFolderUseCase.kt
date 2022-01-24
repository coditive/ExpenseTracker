package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.model.CreateFolderRequest
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager


class SearchOrCreateAppFolderUseCase constructor(
    private val apiRequest: DriveApiRequest,
    private val sharedPrefManager: SharedPrefManager
    ) {

    suspend fun execute() {
        val query = "mimeType = 'application/vnd.google-apps.folder' and name = 'Expense-Tracker'"

        val result = apiRequest.searchFile(
            sharedPrefManager.getUserToken(),
            Constants.apiKey,
            Constants.corpora,
            query
        )

        sharedPrefManager.storeExpenseTrackerFolderId(result.files[0].id)

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
        }

    }
}

