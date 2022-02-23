package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.data.remote.model.SpreadSheetBatchUpdateResponse
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
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
import kotlin.random.Random

class AddDataValidationUseCaseTest {

    private lateinit var useCase: AddDataValidationUseCase
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val provider: GoogleApisClientProvider = mockk()
    private val apiKey = ""
    private val authToken = ""
    private val spreadSheetId = ""
    private val transactionSheetId = Random.nextInt()

    @Before
    fun setUp() {
        useCase = AddDataValidationUseCase(sharedPrefManager, provider, apiKey)
    }

    @Test
    fun `when error is received`() = runBlocking {
        every { sharedPrefManager.getTransactionSheetId() } returns transactionSheetId
        every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
        coEvery {
            provider.sheetApiClient().updateSpreadSheetToFormat(
                spreadSheetId,
                apiKey,
                any()
            )
        } returns Response.error(404, "".toResponseBody())

        val result = useCase.execute()
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when success is received with emptyList of replies`() = runBlocking {
        every { sharedPrefManager.getTransactionSheetId() } returns transactionSheetId
        every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
        coEvery {
            provider.sheetApiClient().updateSpreadSheetToFormat(
                spreadSheetId,
                apiKey,
                any()
            )
        } returns Response.success(SpreadSheetBatchUpdateResponse("", emptyList()))

        val result = useCase.execute()
        print(result)
        assert(result is Success)
    }

}