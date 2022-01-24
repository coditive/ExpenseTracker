package com.syrous.expensetracker.usecase

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.syrous.expensetracker.data.local.CategoriesDao
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.model.UploadFileMetaData
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.Constants.apiKey
import com.syrous.expensetracker.utils.SharedPrefManager
import com.syrous.expensetracker.utils.toUserTransaction
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class UploadUserTransactionUseCase constructor(
    private val transactionManager: TransactionManager,
    private val categoryManager: CategoryManager,
    private val apiRequest: DriveApiRequest,
    private val sharedPrefManager: SharedPrefManager
) {

    private val sdf = SimpleDateFormat(Constants.datePattern, Locale.getDefault())
    private val columnSeparator = ","
    private val lineSeparator = "\r\n"

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun uploadUserTransactionToDrive(context: Context, fileName: String, description: String) {
        val listOfUserTransaction = transactionManager.getAllTransactions()

        val csvFile = convertUserTransactionToCSVFile(
            listOfUserTransaction,
            createCSVFileOnStorage(context, fileName)
        )

        val requestFile = csvFile.readBytes().toRequestBody(
            Constants.spreadSheetMimeType.toMediaType()
        )

        val uploadFileMetaData = UploadFileMetaData(
            fileName,
            Constants.spreadSheetMimeType,
            description,
            listOf(sharedPrefManager.getExpenseTrackerFolderId())
        )
        val multipartBody = MultipartBody.Part.create(requestFile)

        val response = apiRequest.uploadFile(
            sharedPrefManager.getUserToken(),
            apiKey,
            MultipartBody.Part.create(uploadFileMetaData),
            multipartBody
        )
        sharedPrefManager.storeSpreadSheetId(response.id)
    }


    private fun convertUserTransactionToCSVFile(
        listOfDBTransaction: List<UserTransaction>,
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

        for (transaction in listOfDBTransaction) {
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

    private fun addRecordsToFile(recordStringBuilder: StringBuilder, file: File): File {
        val outputStream = FileOutputStream(file)
        outputStream.write(recordStringBuilder.toString().toByteArray())
        outputStream.close()
        return file
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun createCSVFileOnStorage(context: Context, fileName: String): File {
        val file = context.applicationContext.getFileStreamPath(fileName)
        return File(file.path)
    }
}