package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.data.remote.model.SpreadsheetAppendResponse
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody

import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CreateSummarySheetUseCaseTest {

    private lateinit var useCase: CreateSummarySheetUseCase
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val sheetApi: SheetApi = mockk()
    private val apiKey = ""
    private val context: Context = mockk(relaxed = true)
    private val authToken = ""
    private val spreadSheetId = ""
    private val summaryAColumnRange = "Summary!A:A"
    private val summaryBColumnRange = "Summary!B2:B"

    @Before
    fun setUp() {
        useCase = CreateSummarySheetUseCase(sharedPrefManager, sheetApi, apiKey)
    }

    @Test
    fun `when resources returns emptyArray for categories`() = runBlocking {
        every { context.resources.getStringArray(R.array.expense_categories) } returns emptyArray()

        val result = useCase.execute(context)
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when initial append api for createSummarySheet fun is called and error is received`() =
        runBlocking {
            every { context.resources.getStringArray(R.array.expense_categories) } returns buildExpenseCategoriesArray(
                5
            )
            every { sharedPrefManager.getUserToken() } returns authToken
            every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            coEvery {
                sheetApi.appendValueIntoSheet(
                    authToken,
                    spreadSheetId,
                    summaryAColumnRange,
                    apiKey,
                    Constants.overwrite,
                    null,
                    Constants.formattedValue,
                    Constants.userEntered,
                    any()
                )
            } returns Response.error(404, "".toResponseBody())

            val result = useCase.execute(context)
            print(result)
            assert(result is Failure)
        }

    @Test
    fun `when initial append api for createSummarySheet fun is called and success is received and other append call fails `() =
        runBlocking {
            every { context.resources.getStringArray(R.array.expense_categories) } returns buildExpenseCategoriesArray(5)
            every { sharedPrefManager.getUserToken() } returns authToken
            every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            val range = slot<String>()
            coEvery {
                sheetApi.appendValueIntoSheet(
                    authToken,
                    spreadSheetId,
                    capture(range),
                    apiKey,
                    Constants.overwrite,
                    null,
                    any(),
                    Constants.userEntered,
                    any()
                )
            } answers {
                when (range.captured) {
                    summaryAColumnRange -> Response.success(
                        SpreadsheetAppendResponse("", null)
                    )
                    summaryBColumnRange -> Response.error(404, "".toResponseBody())
                    else -> throw Exception()
                }
            }

            val result = useCase.execute(context)
            print(result)
            assert(result is Failure)
        }

    @Test
    fun `when initial append api for createSummarySheet fun is called and success is received and other append call succeed `() =
        runBlocking {
            every { context.resources.getStringArray(R.array.expense_categories) } returns buildExpenseCategoriesArray(
                5
            )
            every { sharedPrefManager.getUserToken() } returns authToken
            every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            val range = slot<String>()
            coEvery {
                sheetApi.appendValueIntoSheet(
                    authToken,
                    spreadSheetId,
                    capture(range),
                    apiKey,
                    Constants.overwrite,
                    null,
                    any(),
                    Constants.userEntered,
                    any()
                )
            } answers {
                when (range.captured) {
                    summaryAColumnRange -> Response.success(
                        SpreadsheetAppendResponse("", null)
                    )
                    summaryBColumnRange -> Response.success(SpreadsheetAppendResponse("", null))
                    else -> {
                        print(range.captured)
                        throw Exception()
                    }
                }
            }
            val result = useCase.execute(context)
            print(result)
            assert(result is Success)
        }


    private fun buildExpenseCategoriesArray(count: Int) = mutableListOf<String>().apply {
        repeat(count) {
            add("AbcD")
        }
    }.toTypedArray()
}