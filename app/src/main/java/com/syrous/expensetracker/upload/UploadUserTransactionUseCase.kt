package com.syrous.expensetracker.upload

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.local.model.UserTransaction
import com.syrous.expensetracker.data.remote.ApiRequest
import com.syrous.expensetracker.data.remote.model.UploadFileMetaData
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class UploadUserTransactionUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
    private val apiRequest: ApiRequest
) {

    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val columnSeparator = ","
    private val lineSeparator = "\r\n"

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun uploadUserTransactionToDrive(context: Context, authToken: String, apiKey: String, fileName: String) {
        val listOfUserTransaction = transactionDao.getAllUserTransactions()
        val csvFile = convertUserTransactionToCSVFile(listOfUserTransaction,
            createCSVFileOnStorage(context, fileName))

        val requestFile = csvFile.readBytes().toRequestBody(
            "application/vnd.google-apps.spreadsheet".toMediaType()
        )

        val uploadFileMetaData = UploadFileMetaData(
            "test-file.csv",
            "application/vnd.google-apps.spreadsheet",
            "test-upload"
        )
        val multipartBody = MultipartBody.Part.create(requestFile)

        apiRequest.uploadFile(authToken, apiKey, MultipartBody.Part.create(uploadFileMetaData), multipartBody)
    }


    private suspend fun convertUserTransactionToCSVFile(
        listOfUserTransaction: List<UserTransaction>,
        file: File
    ): File {
        val listOfRecords = mutableListOf<List<String>>()

        val listOfHeaders = mutableListOf<String>()
        listOfHeaders.add(UserTransaction::id.name)
        listOfHeaders.add(UserTransaction::date.name)
        listOfHeaders.add(UserTransaction::description.name)
        listOfHeaders.add(UserTransaction::amount.name)
        listOfHeaders.add(UserTransaction::transactionCategory.name)
        listOfHeaders.add(UserTransaction::categoryTag.name)

        listOfRecords.add(listOfHeaders)

        for (transaction in listOfUserTransaction) {
            val record = mutableListOf<String>()
            record.add(transaction.id.toString())
            record.add(sdf.format(transaction.date))
            record.add(transaction.description)
            record.add(transaction.amount.toString())
            record.add(transaction.transactionCategory.name)
            record.add(transaction.categoryTag)

            listOfRecords.add(record)
        }

        val recordStringBuilder = convertRecordToCSV(listOfRecords)
        return addRecordsToFile(recordStringBuilder, file)
    }

    private fun convertRecordToCSV(listOfRecords: List<List<String>>): StringBuilder {
        val stringBuilder = StringBuilder()

        for(record in listOfRecords) {
            for(item in record) {
                stringBuilder.append(item)
                stringBuilder.append(columnSeparator)
            }
            stringBuilder.append(lineSeparator)
        }

      return stringBuilder
    }

    private fun addRecordsToFile(recordStringBuilder: StringBuilder, file: File): File {
        val outputStream = FileOutputStream(file)

        outputStream.write(recordStringBuilder.toString().toByteArray())
        outputStream.close()
        try {
            val br = BufferedReader(FileReader(file))
            Log.d("UploadUseCase","File contains -> ${br.readLine()}")
        } catch (e: IOException) {
            Log.d("UploadUseCase", "Exception Caught")
        }

        return file
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createCSVFileOnStorage(context: Context, fileName: String): File {
        val file = context.applicationContext.getFileStreamPath(fileName)
        return File(file.path)
    }
}