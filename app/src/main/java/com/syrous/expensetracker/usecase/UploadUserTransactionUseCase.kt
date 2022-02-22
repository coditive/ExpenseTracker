package com.syrous.expensetracker.usecase

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.data.remote.model.UploadFileMetaData
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.GoogleApisClientProvider
import com.syrous.expensetracker.utils.SharedPrefManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class UploadUserTransactionUseCase @Inject constructor(
    private val transactionManager: TransactionManager,
    private val provider: GoogleApisClientProvider,
    private val sharedPrefManager: SharedPrefManager,
    @Named("apiKey") private val apiKey: String
) {
    private val TAG = this::class.java.name
    private val sdf = SimpleDateFormat(Constants.datePattern, Locale.getDefault())
    private val columnSeparator = ","
    private val lineSeparator = "\r\n"

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun uploadUserTransactionToDrive(
        context: Context,
        fileName: String,
        description: String
    ): UseCaseResult<Boolean> {
        val transactionList = transactionManager.getAllTransactionListFromStorage()
        return try {
            if (transactionList.isNotEmpty()) {
                val csvFile = convertUserTransactionToCSVFile(
                    transactionList,
                    createCSVFileOnStorage(context, fileName)
                )
                val fileRequestBody = csvFile.readBytes().toRequestBody(
                    Constants.spreadSheetMimeType.toMediaType()
                )

                val uploadFileMetaData = UploadFileMetaData(
                    fileName,
                    Constants.spreadSheetMimeType,
                    description,
                    listOf(sharedPrefManager.getExpenseTrackerFolderId())
                )

                val response = provider.driveApiClient().uploadFile(
                    apiKey,
                    MultipartBody.Part.create(uploadFileMetaData),
                    MultipartBody.Part.create(fileRequestBody)
                )

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        sharedPrefManager.storeSpreadSheetId(response.body()!!.id)
                        Log.i(TAG, "file upload Success!!!")
                        Success(true)
                    } else
                        Failure(response.errorBody().toString())
                } else
                    Failure(response.message())
            } else Success(true)
        } catch (e: Exception) {
            Failure(e.message!!)
        }
    }


    private fun convertUserTransactionToCSVFile(
        listOfUserTransaction: List<UserTransaction>,
        file: File
    ): File {
        val listOfRecords = mutableListOf<List<String>>()

        val listOfHeaders = mutableListOf<String>()
        listOfHeaders.add(UserTransaction::id.name)
        listOfHeaders.add(UserTransaction::date.name)
        listOfHeaders.add(UserTransaction::description.name)
        listOfHeaders.add(UserTransaction::amount.name)
        listOfHeaders.add(UserTransaction::category.name)
        listOfHeaders.add(UserTransaction::categoryTag.name)

        listOfRecords.add(listOfHeaders)

        for (transaction in listOfUserTransaction) {
            val record = mutableListOf<String>()
            record.add(transaction.id.toString())
            record.add(sdf.format(transaction.date))
            record.add(transaction.description)
            record.add(transaction.amount.toString())
            record.add(transaction.category.name)
            record.add(transaction.categoryTag)

            listOfRecords.add(record)
        }

        val recordStringBuilder = convertRecordToCSV(listOfRecords)
        return addRecordsToFile(recordStringBuilder, file)
    }

    private fun convertRecordToCSV(listOfRecords: List<List<String>>): StringBuilder {
        val stringBuilder = StringBuilder()

        for (record in listOfRecords) {
            for (item in record) {
                stringBuilder.append(item)
                stringBuilder.append(columnSeparator)
            }
            stringBuilder.append(lineSeparator)
        }

        return stringBuilder
    }

    @Throws(IOException::class)
    private fun addRecordsToFile(recordStringBuilder: StringBuilder, file: File): File {
        val outputStream = FileOutputStream(file)
        outputStream.write(recordStringBuilder.toString().toByteArray())
        outputStream.close()
        return file
    }

    @Throws(IOException::class)
    @RequiresApi(Build.VERSION_CODES.R)
    private fun createCSVFileOnStorage(context: Context, fileName: String): File {
        val file = context.applicationContext.getFileStreamPath(fileName)
        return File(file.path)
    }
}