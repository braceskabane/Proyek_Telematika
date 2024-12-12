package com.dicoding.hanebado.core.domain.histories.interactor

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.histories.model.HistoryDomain
import com.dicoding.hanebado.core.domain.histories.repository.IHistoryRepository
import com.dicoding.hanebado.core.domain.histories.usecase.HistoryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryInteractor @Inject constructor(private val historyRepository: IHistoryRepository) :
    HistoryUseCase {
    override fun getHistory(): Flow<Resource<List<HistoryDomain>>> {
        return historyRepository.getHistory()
    }
}
