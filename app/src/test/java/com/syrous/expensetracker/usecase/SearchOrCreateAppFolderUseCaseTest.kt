package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.data.remote.model.BasicFileMetaData
import com.syrous.expensetracker.data.remote.model.CreateFolderRequest
import com.syrous.expensetracker.data.remote.model.SearchFileQueryResponse
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.GoogleApisClientProvider
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class SearchOrCreateAppFolderUseCaseTest {

    private lateinit var useCase: SearchOrCreateAppFolderUseCase
    private val provider: GoogleApisClientProvider = mockk()
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val apiKey = ""
    private val authToken = ""

    @Before
    fun setUp() {
        useCase = SearchOrCreateAppFolderUseCase(provider, sharedPrefManager, apiKey)
    }

    @Test
    fun `when initial search for folder is requested and Error is received`() = runBlocking {
        coEvery {
            provider.driveApiClient().searchFile(apiKey, Constants.corpora, any())
        } returns Response.error(404, "".toResponseBody())

        val result = useCase.execute()
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when initial search for folder is requested and success is received but body is null`() =
        runBlocking {
            coEvery {
                provider.driveApiClient().searchFile( apiKey, Constants.corpora, any())
            } returns Response.success(null)
            val result = useCase.execute()
            print(result)
            assert(result is Failure)
        }

    @Test
    fun `when initial search for folder is requested and success is received with body, file list is not empty`() =
        runBlocking {
            coEvery {
                provider.driveApiClient().searchFile(apiKey, Constants.corpora, any())
            } returns Response.success(
                SearchFileQueryResponse(
                    "",
                    false,
                    buildBasicFileMetaData(1)
                )
            )
            val folderId = mutableListOf<String>()
            every { sharedPrefManager.storeExpenseTrackerFolderId(capture(folderId)) } returns Unit

            val result = useCase.execute()
            print(result)
            assert(result is Success && folderId[0] == buildBasicFileMetaData(1)[0].id)
        }

    @Test
    fun `when initial search for folder is requested and success is received with body, file list is empty`() =
        runBlocking {
            coEvery {
                provider.driveApiClient().searchFile(apiKey, Constants.corpora, any())
            } returns Response.success(SearchFileQueryResponse("", false, emptyList()))
            coEvery {
                provider.driveApiClient().createFolder(
                    apiKey,
                    CreateFolderRequest(Constants.appName, Constants.folderMimeType)
                )
            } returns Response.success(buildBasicFileMetaData(1)[0])
            val folderId = mutableListOf<String>()
            every { sharedPrefManager.storeExpenseTrackerFolderId(capture(folderId)) } returns Unit
            val result = useCase.execute()
            print(result)
            assert(result is Success && folderId[0] == buildBasicFileMetaData(1)[0].id)
        }

    private fun buildBasicFileMetaData(count: Int) = mutableListOf<BasicFileMetaData>().apply {
        repeat(count) {
            add(BasicFileMetaData("", "abcd", "", ""))
        }
    }
}