package com.dicoding.hanebado.core.domain.dailyplan.repository

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.DailyPlanRequest
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanListDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayDailyPlanDomain
import kotlinx.coroutines.flow.Flow

interface IDailyPlanRepository {
    fun addDailyPlan(dailyPlanRequest: DailyPlanRequest): Flow<Resource<DailyPlanDomain>>
    fun getAllDailyPlan(): Flow<Resource<DailyPlanListDomain>>
    fun getTodayDailyPlan(): Flow<Resource<TodayDailyPlanDomain>>
}