package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.SpreadsheetAppendResponse
import com.syrous.expensetracker.data.remote.model.ValuesRequest
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.usecase.UseCaseResult.*
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.random.Random


class AppendTransactionsUseCaseTest {
    lateinit var appendUseCase: AppendTransactionsUseCase
    private val transactionManager: TransactionManager = mockk()
    private val sheetApiRequest: SheetApiRequest = mockk(relaxed = true)
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val authToken = ""
    private val spreadSheetId = ""
    private val range = "Transactions!A:F"
    private val apiKey = ""
    private val insertDataOption = Constants.overwrite
    private val responseDateTimeRenderOption = null
    private val responseValueRenderOption = Constants.formattedValue
    private val valueInputOption = Constants.userEntered
    private val valuesRequest = ValuesRequest(emptyList())

    @Before
    fun setUp() {
        appendUseCase =
            AppendTransactionsUseCase(transactionManager, sheetApiRequest, sharedPrefManager, apiKey)
    }

    @Test
    fun `when room is empty don't call api`() = runBlocking {
        coEvery { transactionManager.getUnSyncedTransaction() } returns emptyList()
        coEvery {
            sheetApiRequest.appendValueIntoSheet(
                authToken,
                spreadSheetId,
                range,
                apiKey,
                insertDataOption,
                responseDateTimeRenderOption,
                responseValueRenderOption,
                valueInputOption,
                valuesRequest
            )
        } returns Response.error(404, "".toResponseBody())

        val result = appendUseCase.execute()

        assert(result is SucceedNoResultRequired)
    }

    @Test
    fun `when user transaction list is given call api then failure received`() =
        runBlocking {
            coEvery { transactionManager.getUnSyncedTransaction() } returns buildUnSyncTransaction(2)
            coEvery {
                sheetApiRequest.appendValueIntoSheet(
                    authToken,
                    spreadSheetId,
                    range,
                    apiKey,
                    insertDataOption,
                    responseDateTimeRenderOption,
                    responseValueRenderOption,
                    valueInputOption,
                    valuesRequest
                )
            } returns Response.error(404, "".toResponseBody())

            val result = appendUseCase.execute()

            assert(result is Failure)
        }


    @Test
    fun `when user transaction list is given, called api then success but null body is received`() = runBlocking {
        coEvery { transactionManager.getUnSyncedTransaction() } returns buildUnSyncTransaction(2)
        coEvery {
            sheetApiRequest.appendValueIntoSheet(
                authToken,
                spreadSheetId,
                range,
                apiKey,
                insertDataOption,
                responseDateTimeRenderOption,
                responseValueRenderOption,
                valueInputOption,
                valuesRequest
            )
        } returns Response.success(null)

        val result = appendUseCase.execute()

        assert(result is Failure)
    }


    @Test
    fun `when user transaction list is given, call api then succeed received`() =
        runBlocking {
            val mutableValueRequestList = mutableListOf<ValuesRequest>()
            every { sharedPrefManager.getUserToken() } returns authToken
            every { sharedPrefManager.getSpreadSheetId() } returns spreadSheetId
            coEvery { transactionManager.getUnSyncedTransaction() } returns buildUnSyncTransaction(5)
            coEvery {
                sheetApiRequest.appendValueIntoSheet(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    capture(mutableValueRequestList)
                )
            } returns Response.success(SpreadsheetAppendResponse("", ""))

            coEvery { transactionManager.updateTransactionListSyncStatus(any()) } returns Unit

            val result = appendUseCase.execute()

            assert(result is Success) {
                print(mutableValueRequestList)
            }
        }

    private fun buildUnSyncTransaction(count: Int) = mutableListOf<UserTransaction>().apply {
        repeat(count) {
            add(
                UserTransaction(
                    id = abs(Random.nextLong()),
                    amount = abs(Random.nextInt()),
                    description = "",
                    date = Date(0L),
                    category = Category.EXPENSE,
                    categoryTag = ""
                )
            )
        }
    }
}