package com.syrous.expensetracker.usecase

sealed class UseCaseResult<out T> {

    data class Success<T> (val boolean: T): UseCaseResult<T>()

    class SucceedNoResultRequired<T>: UseCaseResult<T>()

    data class Failure<T>(val message: String): UseCaseResult<T>()

}