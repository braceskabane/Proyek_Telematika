package com.dicoding.hanebado.core.data.source

import com.dicoding.hanebado.core.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<ResultType, RequestType> {
    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        try {
            // Hapus first() agar flow tidak berhenti di loading
            createCall().collect { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Success -> {
                        emit(Resource.Success(fetchFromApi(apiResponse.data)))
                    }
                    is ApiResponse.Error -> {
                        onFetchFailed()
                        emit(Resource.Error(apiResponse.exception.toString()))
                    }
                    is ApiResponse.Empty -> {
                        emit(Resource.Message("Empty"))
                    }
                    is ApiResponse.Loading -> {
                        emit(Resource.Loading())
                    }
                }
            }
        } catch (e: Exception) {
            onFetchFailed()
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract suspend fun fetchFromApi(response: RequestType): ResultType

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    fun asFlow(): Flow<Resource<ResultType>> = result
}