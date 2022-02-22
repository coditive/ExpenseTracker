package com.syrous.expensetracker.data.remote

import com.syrous.expensetracker.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface SheetApi {

    @GET("{spreadSheetId}")
    suspend fun getSpreadSheetData(
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String
    ): Response<SpreadSheetResponse>

    @POST("{spreadSheetId}:batchUpdate")
    suspend fun updateSpreadSheetToFormat(
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String,
        @Body spreadSheetBatchUpdateRequest: SpreadSheetBatchUpdateRequest
    ): Response<SpreadSheetBatchUpdateResponse>

    @POST("{spreadSheetId}/values/{range}:append")
    suspend fun appendValueIntoSheet(
        @Path("spreadSheetId") spreadSheetId: String,
        @Path("range") range: String,
        @Query("key") apiKey: String,
        @Query("insertDataOption") insertDataOption: String,
        @Query("responseDateTimeRenderOption") responseDateTimeRenderOption: String?,
        @Query("responseValueRenderOption") responseValueRenderOption: String,
        @Query("valueInputOption") valueInputOption: String,
        @Body valuesRequest: ValuesRequest
    ): Response<SpreadsheetAppendResponse>

    @POST("{spreadSheetId}/values:batchUpdate")
    suspend fun updateValueIntoSheet(
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String,
        @Body uploadValueRequest: UpdateValueRequest
    )
}