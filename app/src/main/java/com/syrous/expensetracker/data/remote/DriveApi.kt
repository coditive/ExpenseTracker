package com.syrous.expensetracker.data.remote

import com.syrous.expensetracker.data.remote.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DriveApi {

    @Multipart
    @POST("upload/drive/v3/files")
    suspend fun uploadFile(
        @Query("key") apiKey: String,
        @Part(encoding = "8-bit") uploadFileMetaData: MultipartBody.Part,
        @Part(encoding = "base64") file: MultipartBody.Part
    ): Response<BasicFileMetaData>

    @POST("drive/v3/files")
    @Headers("Content-Type: application/json")
    suspend fun createFolder(
        @Query("key") apiKey: String,
        @Body request: CreateFolderRequest
    ): Response<BasicFileMetaData>

    @GET("drive/v3/files")
    suspend fun searchFile(
        @Query("key") apiKey: String,
        @Query("corpora") corpora: String,
        @Query("q") query: String
    ): Response<SearchFileQueryResponse>

}