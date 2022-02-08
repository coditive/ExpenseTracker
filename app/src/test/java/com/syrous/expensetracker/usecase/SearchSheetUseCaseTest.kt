package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.model.BasicFileMetaData
import com.syrous.expensetracker.data.remote.model.SearchFileQueryResponse
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import retrofit2.Response
import javax.inject.Named

class SearchSheetUseCaseTest {

    private lateinit var useCase: SearchSheetUseCase
    private val apiRequest: DriveApiRequest = mockk()
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val apiKey = ""
    private val authToken = ""

    @Before
    fun setUp() {
        useCase = SearchSheetUseCase(apiRequest, sharedPrefManager, apiKey)
    }

    @Test
    fun `when api sends error response`() = runBlocking {
        coEvery {
            apiRequest.searchFile(
                authToken,
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.error(404, "".toResponseBody())
        every { sharedPrefManager.getUserToken() } returns authToken

        val result = useCase.execute()
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when api sends success response but body is empty`() = runBlocking {
        coEvery {
            apiRequest.searchFile(
                authToken,
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.success(null)
        every { sharedPrefManager.getUserToken() } returns authToken

        val result = useCase.execute()
        print(result)
        assert(result is Failure)
    }


    @Test
    fun `when api sends success response with body but files is emptyList`() = runBlocking {
        coEvery {
            apiRequest.searchFile(
                authToken,
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.success(SearchFileQueryResponse("", false, emptyList()))
        every { sharedPrefManager.getUserToken() } returns authToken
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
            apiRequest.searchFile(
                authToken,
                apiKey,
                Constants.corpora,
                any()
            )
        } returns Response.success(SearchFileQueryResponse("", false, buildBasicFileMetaData(1)))
        every { sharedPrefManager.getUserToken() } returns authToken
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