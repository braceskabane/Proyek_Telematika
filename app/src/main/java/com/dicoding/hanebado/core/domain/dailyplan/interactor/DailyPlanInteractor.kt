package com.dicoding.hanebado.core.domain.dailyplan.interactor

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.DailyPlanRequest
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanListDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayDailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.repository.IDailyPlanRepository
import com.dicoding.hanebado.core.domain.dailyplan.usecase.DailyPlanUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyPlanInteractor @Inject constructor(
    private val dailyPlanRepository: IDailyPlanRepository
) : DailyPlanUseCase {

    override fun addDailyPlan(dailyPlanRequest: DailyPlanRequest): Flow<Resource<DailyPlanDomain>> {
        return dailyPlanRepository.addDailyPlan(dailyPlanRequest)
    }

    override fun getAllDailyPlan(): Flow<Resource<DailyPlanListDomain>> {
        return dailyPlanRepository.getAllDailyPlan()
    }
    override fun getTodayDailyPlan(): Flow<Resource<List<TodayDailyPlanDomain>>> {
        return dailyPlanRepository.getTodayDailyPlan()
    }
}