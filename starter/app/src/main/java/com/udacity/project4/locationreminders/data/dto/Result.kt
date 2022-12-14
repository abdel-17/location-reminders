package com.udacity.project4.locationreminders.data.dto

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val errorMessage: String?, val statusCode: Int? = null) : Result<Nothing>()
}