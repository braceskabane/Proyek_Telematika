package com.dicoding.hanebado.core.domain.histories.usecase

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.histories.model.HistoryDomain
import kotlinx.coroutines.flow.Flow

interface HistoryUseCase {
    fun getHistory(): Flow<Resource<List<HistoryDomain>>>
}