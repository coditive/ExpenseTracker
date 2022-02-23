package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.data.remote.model.SpreadsheetAppendResponse
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

class AddCategoriesToSheetUseCaseTest {

    private lateinit var useCase: AddCategoriesToSheetUseCase
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val provider: GoogleApisClientProvider = mockk()
    private val apiKey = ""
    private val context: Context = mockk(relaxed = true)
    private val authToken = ""
    private val spreadSheetId = ""
    private val categoriesAColumnRange = "Categories!A:A"

    @Before
    fun setUp() {
        useCase = AddCategoriesToSheetUseCase(sharedPrefManager, provider, apiKey)
    }

    @Test
    fun `when resources returns emptyArray for Expense categories`() = runBlocking {
        every { context.resources.getStringArray(R.array.expense_categories) } returns emptyArray()
        every { context.resources.getStringArray(R.array.income_categories) } returns buildExpenseCategoriesArray(5)
        val result = useCase.execute(context)
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when resources returns emptyArray for Income categories`() = runBlocking {
        every { context.resources.getStringArray(R.array.expense_categories) } returns buildExpenseCategoriesArray(5)
        every { context.resources.getStringArray(R.array.income_categories) } returns emptyArray()
        val result = useCase.execute(context)
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when api request sent and error is received`() = runBlocking {
        every { context.resources.getStringArray(R.array.expense_categories) } returns buildExpenseCategoriesArray(5)
        every { context.resources.getStringArray(R.array.income_categories) } returns emptyArray()
        every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
        coEvery {
            provider.sheetApiClient().appendValueIntoSheet(
                spreadSheetId,
                categoriesAColumnRange,
                apiKey,
                Constants.overwrite,
                null,
                Constants.unformattedValue,
                Constants.userEntered,
                any()
            )
        } returns Response.error(404, "".toResponseBody())

        val result = useCase.execute(context)
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when api request sent and success is received`() = runBlocking {
        every { context.resources.getStringArray(R.array.expense_categories) } returns buildExpenseCategoriesArray(5)
        every { context.resources.getStringArray(R.array.income_categories) } returns buildExpenseCategoriesArray(2)
        every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
        coEvery {
            provider.sheetApiClient().appendValueIntoSheet(
                spreadSheetId,
                categoriesAColumnRange,
                apiKey,
                Constants.overwrite,
                null,
                Constants.unformattedValue,
                Constants.userEntered,
                any()
            )
        } returns Response.success(SpreadsheetAppendResponse("", null))

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