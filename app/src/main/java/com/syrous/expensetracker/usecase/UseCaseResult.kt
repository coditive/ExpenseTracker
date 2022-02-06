package com.syrous.expensetracker.usecase

sealed class UseCaseResult {

    data class Success(val boolean: Boolean): UseCaseResult()

    data class Failure(val message: String): UseCaseResult()
}