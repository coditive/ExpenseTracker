package com.syrous.expensetracker.data.remote

import com.syrous.expensetracker.data.remote.model.*
import retrofit2.http.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface SheetApiRequest {

    @GET("{spreadSheetId}")
    suspend fun getSpreadSheetData(
        @Header("Authorization") authToken: String,
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String
    ): SpreadSheetResponse

    @POST("{spreadSheetId}:batchUpdate")
    suspend fun updateSpreadSheetToFormat(
        @Header("Authorization") authToken: String,
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String,
        @Body spreadSheetBatchUpdateRequest: SpreadSheetBatchUpdateRequest
    )

    @POST("{spreadSheetId}/values/{range}:append")
    suspend fun appendValueIntoSheet(
        @Header("Authorization") authToken: String,
        @Path("spreadSheetId") spreadSheetId: String,
        @Path("range") range: String,
        @Query("key") apiKey: String,
        @Query("insertDataOption") insertDataOption: String,
        @Query("responseDateTimeRenderOption") responseDateTimeRenderOption: String?,
        @Query("responseValueRenderOption") responseValueRenderOption: String,
        @Query("valueInputOption") valueInputOption: String,
        @Body valuesRequest: ValuesRequest
    )

    @POST("{spreadSheetId}/values:batchUpdate")
    suspend fun updateValueIntoSheet(
        @Header("Authorization") authToken: String,
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String,
        @Body uploadValueRequest: UpdateValueRequest
    )


}