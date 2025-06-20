package com.ryen.sunnah_alhadi.data.util

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : RepositoryResult<Nothing>()
}