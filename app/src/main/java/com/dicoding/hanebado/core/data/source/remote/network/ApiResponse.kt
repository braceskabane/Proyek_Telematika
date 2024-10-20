package com.dicoding.hanebado.core.data.source.remote.network

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val exception: Throwable) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
    object Empty : ApiResponse<Nothing>()
}