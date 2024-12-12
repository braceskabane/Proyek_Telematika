package com.dicoding.hanebado.core.utils.datamapper

import com.dicoding.hanebado.core.data.source.remote.response.histories.HistoryResponse
import com.dicoding.hanebado.core.domain.histories.model.HistoryDomain

object HistoryMapper {
    fun mapResponseToDomain(response: HistoryResponse): List<HistoryDomain> {
        return response.data?.mapNotNull {
            // Assuming each data item can be cast or mapped to the appropriate domain model.
            // Replace `Any?` with the actual type if available, and map its fields accordingly.
            val meta = response.meta
            HistoryDomain(
                id = null, // Assign an ID if applicable
                totalDuration = meta?.stats?.totalDuration,
                totalSessions = meta?.stats?.totalSessions,
                inProgress = meta?.stats?.inProgress,
                uncompleted = meta?.stats?.uncompleted,
                completed = meta?.stats?.completed,
                averageDuration = meta?.stats?.averageDuration,
                page = meta?.pagination?.page,
                limit = meta?.pagination?.limit,
                totalPages = meta?.pagination?.totalPages,
                hasNextPage = meta?.pagination?.hasNextPage,
                hasPreviousPage = meta?.pagination?.hasPreviousPage
            )
        } ?: emptyList()
    }
}
