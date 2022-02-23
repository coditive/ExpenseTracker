package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.data.remote.model.BasicFileMetaData
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

class SearchSheetUseCaseTest {

    private lateinit var useCase: SearchSheetUseCase
    private val provider: GoogleApisClientProvider = mockk()
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val apiKey = ""
    private val authToken = ""

    @Before
    fun setUp() {
        useCase = SearchSheetUseCase(provider, sharedPrefManager, apiKey)
    }

    @Test
    fun `when api sends error response`() = runBlocking {
        coEvery {
            provider.driveApiClient().searchFile(
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.error(404, "".toResponseBody())

        val result = useCase.execute()
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when api sends success response but body is empty`() = runBlocking {
        coEvery {
            provider.driveApiClient().searchFile(
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.success(null)

        val result = useCase.execute()
        print(result)
        assert(result is Failure)
    }


    @Test
    fun `when api sends success response with body but files is emptyList`() = runBlocking {
        coEvery {
            provider.driveApiClient().searchFile(
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.success(SearchFileQueryResponse("", false, emptyList()))
        val captureStoreFileUploadedStatusValue = mutableListOf<Boolean>()
        every {
            sharedPrefManager.storeFileUploadedStatus(
                capture(
                    captureStoreFileUploadedStatusValue
                )
            )
        } returns Unit

        val result = useCase.execute()
        print(result)
        assert(result is Success && !captureStoreFileUploadedStatusValue[0])
    }

    @Test
    fun `when api sends success response with body but files is not emptyList`() = runBlocking {
        coEvery {
            provider.driveApiClient().searchFile(
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.success(SearchFileQueryResponse("", false, buildBasicFileMetaData(1)))
        val captureStoreFileUploadedStatusValue = mutableListOf<Boolean>()
        every { sharedPrefManager.storeSpreadSheetId(any()) } returns Unit
        every {
            sharedPrefManager.storeFileUploadedStatus(
                capture(
                    captureStoreFileUploadedStatusValue
                )
            )
        } returns Unit

        val result = useCase.execute()
        print(result)
        assert(result is Success && captureStoreFileUploadedStatusValue[0])
    }

    private fun buildBasicFileMetaData(count: Int) = mutableListOf<BasicFileMetaData>().apply {
        repeat(count) {
            add(BasicFileMetaData("", "", "", ""))
        }
    }
}