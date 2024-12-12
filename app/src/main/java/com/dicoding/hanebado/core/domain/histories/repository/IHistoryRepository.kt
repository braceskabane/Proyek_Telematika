package com.dicoding.hanebado.core.domain.histories.repository

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.histories.model.HistoryDomain
import kotlinx.coroutines.flow.Flow

interface IHistoryRepository {
    fun getHistory(): Flow<Resource<List<HistoryDomain>>>
}