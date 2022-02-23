package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.data.remote.model.*
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
import kotlin.math.abs
import kotlin.random.Random

class GetAndUpdateSheetsUseCaseTest {

    private lateinit var useCase: GetAndUpdateSheetsUseCase
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val provider: GoogleApisClientProvider = mockk()
    private val authToken = ""
    private val spreadSheetId = ""
    private val apiKey = ""

    @Before
    fun setUp() {
        useCase = GetAndUpdateSheetsUseCase(sharedPrefManager, provider, apiKey)
    }

    @Test
    fun `when api sends error response for getSpreadSheetData to valid request`() = runBlocking {
        coEvery { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
        coEvery {
            provider.sheetApiClient().getSpreadSheetData(
                spreadSheetId,
                apiKey
            )
        } returns Response.error(404, "".toResponseBody())

        val result = useCase.execute()
        print(result)
        assert(result is Failure)
    }


    @Test
    fun `when api send success response But sheetList is empty for getSpreadSheetData to valid request`() =
        runBlocking {
            coEvery { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            coEvery {
                provider.sheetApiClient().getSpreadSheetData(
                    spreadSheetId,
                    apiKey
                )
            } returns Response.success(SpreadSheetResponse("", "", emptyList()))

            val result = useCase.execute()
            print(result)
            assert(result is Failure)
        }


    @Test
    fun `when api send success response But body is null for getSpreadSheetData to valid request`() =
        runBlocking {
            coEvery { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            coEvery {
                provider.sheetApiClient().getSpreadSheetData(
                    spreadSheetId,
                    apiKey
                )
            } returns Response.success(null)
            val result = useCase.execute()
            print(result)
            assert(result is Failure)
        }

    @Test
    fun `when api send success response for getSpreadSheetData but error for updateSpreadSheetToFormat`() =
        runBlocking {
            coEvery { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            coEvery {
                provider.sheetApiClient().getSpreadSheetData(
                    any(),
                    any()
                )
            } returns Response.success(SpreadSheetResponse("", "", buildSheetDetailList(2)))
            every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            every { sharedPrefManager.getTransactionSheetId() } returns 0
            every { sharedPrefManager.getSummarySheetId() } returns 0
            every { sharedPrefManager.getCategoriesSheetId() } returns 0
            every { sharedPrefManager.storeTransactionSheetId(any()) } returns Unit
            every { sharedPrefManager.storeSummarySheetId(any()) } returns Unit
            every { sharedPrefManager.storeCategoriesSheetId(any()) } returns Unit
            coEvery {
                provider.sheetApiClient().updateSpreadSheetToFormat(
                    spreadSheetId, apiKey, any()
                )
            } returns Response.error(404, "".toResponseBody())

            val result = useCase.execute()
            print(result)
            assert(result is Failure)
        }


    @Test
    fun `when api send success response for getSpreadSheetData but success for updateSpreadSheetToFormat`() = runBlocking {
        coEvery { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
        coEvery {
            provider.sheetApiClient().getSpreadSheetData(
                any(),
                any()
            )
        } returns Response.success(SpreadSheetResponse("", "", buildSheetDetailList(2)))
        every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
        every { sharedPrefManager.getTransactionSheetId() } returns 0
        every { sharedPrefManager.getSummarySheetId() } returns 0
        every { sharedPrefManager.getCategoriesSheetId() } returns 0
        every { sharedPrefManager.storeTransactionSheetId(any()) } returns Unit
        every { sharedPrefManager.storeSummarySheetId(any()) } returns Unit
        every { sharedPrefManager.storeCategoriesSheetId(any()) } returns Unit
        coEvery {
            provider.sheetApiClient().updateSpreadSheetToFormat(
                spreadSheetId, apiKey, any()
            )
        } returns Response.success(SpreadSheetBatchUpdateResponse("", emptyList()))

        val result = useCase.execute()
        print(result)
        assert(result is Success)
    }

    private fun buildSheetDetailList(count: Int) = mutableListOf<GetSheetResponse>().apply {
        repeat(count) {
            add(
                GetSheetResponse(
                    properties = SheetProperties(
                        sheetId = abs(Random.nextInt()),
                        title = "",
                        index = 1,
                        sheetType = "",
                        gridProperties = null
                    )
                )
            )
        }
    }

}