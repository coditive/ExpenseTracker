package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.data.remote.model.BasicFileMetaData
import com.syrous.expensetracker.data.remote.model.CreateFolderRequest
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.GoogleApisClientProvider
import com.syrous.expensetracker.utils.SharedPrefManager
import javax.inject.Inject
import javax.inject.Named


class SearchOrCreateAppFolderUseCase @Inject constructor(
    private val provider: GoogleApisClientProvider,
    private val sharedPrefManager: SharedPrefManager,
    @Named("apiKey") private val apiKey: String
) {
    private val TAG = this::class.java.name
    suspend fun execute(): UseCaseResult<BasicFileMetaData> {
        val query = "mimeType = '${Constants.folderMimeType}' and name = '${Constants.appName}'"

        val result = provider.driveApiClient().searchFile(
            apiKey,
            Constants.corpora,
            query
        )
        return if (result.isSuccessful) {
            if (result.body() != null) {
                if (result.body()!!.files.isEmpty()) {
                    val folder = provider.driveApiClient().createFolder(
                        apiKey,
                        CreateFolderRequest(
                            Constants.appName,
                            Constants.folderMimeType
                        )
                    )
                    if (folder.isSuccessful) {
                        if (folder.body() != null) {
                           sharedPrefManager.storeExpenseTrackerFolderId(folder.body()!!.id)
                            Success(folder.body()!!)
                        } else
                            Failure(folder.errorBody().toString())
                    } else
                        Failure(folder.message())
                } else {
                    result.body()!!.files[0]
                        .let { sharedPrefManager.storeExpenseTrackerFolderId(it.id) }
//                    Log.i(TAG, "Folder found Success!!!")
                    Success(result.body()!!.files[0])
                }
            } else
                Failure(result.errorBody().toString())
        } else
            Failure(result.message())
    }
}


