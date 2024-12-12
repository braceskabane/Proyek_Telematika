package com.dicoding.hanebado.core.data.repository

import com.dicoding.hanebado.core.data.source.NetworkBoundResource
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.data.source.remote.RemoteDataSource
import com.dicoding.hanebado.core.data.source.remote.network.ApiResponse
import com.dicoding.hanebado.core.data.source.remote.response.histories.HistoryResponse
import com.dicoding.hanebado.core.domain.histories.model.HistoryDomain
import com.dicoding.hanebado.core.domain.histories.repository.IHistoryRepository
import com.dicoding.hanebado.core.utils.datamapper.HistoryMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IHistoryRepository {

    override fun getHistory(): Flow<Resource<List<HistoryDomain>>> {
        return object : NetworkBoundResource<List<HistoryDomain>, HistoryResponse>() {
            override suspend fun fetchFromApi(response: HistoryResponse): List<HistoryDomain> {
                return HistoryMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<HistoryResponse>> {
                return remoteDataSource.getHistory()
            }

            override fun onFetchFailed() {
                // Handle failure if needed
            }
        }.asFlow()
    }
}