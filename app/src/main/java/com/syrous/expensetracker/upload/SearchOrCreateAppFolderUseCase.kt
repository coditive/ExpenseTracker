package com.syrous.expensetracker.upload

import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.model.CreateFolderRequest
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject





class SearchOrCreateAppFolderUseCase constructor(
    private val apiRequest: DriveApiRequest,
    private val sharedPrefManager: SharedPrefManager,
    private val coroutineScope: CoroutineScope
) {

    fun execute() {
        val query = "mimeType = 'application/vnd.google-apps.folder' and name = 'Expense-Tracker'"

        coroutineScope.launch {
           val result =  apiRequest.searchFile(
                sharedPrefManager.getUserToken(),
                Constants.apiKey,
                "user",
                query
            )

            if(result.files.isEmpty()) {
                val folder = apiRequest.createFolder(
                    sharedPrefManager.getUserToken(),
                    Constants.apiKey,
                    CreateFolderRequest(
                        "Expense-Tracker",
                        "application/vnd.google-apps.folder"
                    )
                )
            }
        }
    }

}