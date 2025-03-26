package com.example.socialapp.common.util

import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException


// a generic wrapper to place calls in the
internal suspend fun <T> safeApiCall(
    dispatcher: DispatcherProvider,
    errorHandler: (Throwable) -> Result<T> = { defaultErrorHandler(it) },
    apiCall: suspend () -> Result<T>
): Result<T> = withContext(dispatcher.io) {
    try {
        apiCall()
    }catch (exception: Throwable){
        // important this is used to cancel coroutine to avoid breaking
        if (exception is CancellationException) throw exception
        errorHandler(exception)
    }
}

// convert errors into comment errors in our domain
private fun <T> defaultErrorHandler(error: Throwable): Result<T>{
    return if (error is IOException){
        Result.Error(message = Constants.NO_INTERNET_ERROR)
    }else{
        println("THE ERROR MESSAGE ${error.message}")
        Result.Error(message = error.message ?: Constants.UNEXPECTED_ERROR)
    }
}