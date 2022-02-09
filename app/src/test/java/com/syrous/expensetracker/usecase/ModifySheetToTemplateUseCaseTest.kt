package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.SpreadSheetBatchUpdateResponse
import com.syrous.expensetracker.data.remote.model.SpreadsheetAppendResponse
import com.syrous.expensetracker.data.remote.model.ValuesRequest
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.InternalPlatformDsl.toArray
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.internal.runners.statements.Fail
import retrofit2.Response
import javax.inject.Named

class ModifySheetToTemplateUseCaseTest {

    private lateinit var useCase: ModifySheetToTemplateUseCase
    private val createSummarySheetUseCase: CreateSummarySheetUseCase = mockk()
    private val addDataValidationUseCase: AddDataValidationUseCase = mockk()
    private val addCategoriesToSheetUseCase: AddCategoriesToSheetUseCase = mockk()
    private val context: Context = mockk()

    @Before
    fun setUp() {
        useCase = ModifySheetToTemplateUseCase(
            createSummarySheetUseCase,
            addCategoriesToSheetUseCase,
            addDataValidationUseCase
        )
    }

    @Test
    fun `when createSummarySheetUseCase is failed`() = runBlocking {
        coEvery { createSummarySheetUseCase.execute(context) } returns Failure("")

        val result = useCase.execute(context)
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when createSummarySheetUseCase is Passed && AddCategoriesToSheetUseCase is Failed`() =
        runBlocking {
            coEvery { createSummarySheetUseCase.execute(context) } returns Success(
                SpreadsheetAppendResponse("", null)
            )
            coEvery { addCategoriesToSheetUseCase.execute(context) } returns Failure("")
            val result = useCase.execute(context)
            print(result)
            assert(result is Failure)
        }

    @Test
    fun `when createSummarySheetUseCase is Passed && AddCategoriesToSheetUseCase is passed && AddDataValidationUseCase is Failed`() =
        runBlocking {
            coEvery { createSummarySheetUseCase.execute(context) } returns Success(
                SpreadsheetAppendResponse("", null)
            )
            coEvery { addCategoriesToSheetUseCase.execute(context) } returns Success(
                SpreadsheetAppendResponse("", null)
            )
            coEvery { addDataValidationUseCase.execute() } returns Failure("")

            val result = useCase.execute(context)
            print(result)
            assert(result is Failure)
        }

    @Test
    fun `when createSummarySheetUseCase is Passed && AddCategoriesToSheetUseCase is passed && AddDataValidationUseCase is Passed`() = runBlocking {
        coEvery { createSummarySheetUseCase.execute(context) } returns Success(
            SpreadsheetAppendResponse("", null)
        )
        coEvery { addCategoriesToSheetUseCase.execute(context) } returns Success(
            SpreadsheetAppendResponse("", null)
        )
        coEvery { addDataValidationUseCase.execute() } returns Success(
            SpreadSheetBatchUpdateResponse("", emptyList())
        )

        val result = useCase.execute(context)
        print(result)
        assert(result is Success)
    }

}