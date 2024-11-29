package com.dicoding.hanebado.core.data.repository

import android.util.Log
import com.dicoding.hanebado.core.data.source.NetworkBoundResource
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.data.source.remote.RemoteDataSource
import com.dicoding.hanebado.core.data.source.remote.network.ApiResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.AddDailyPlanResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.DailyPlanRequest
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.GetAllDailyResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.GetTodayResponse
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanListDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayDailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.repository.IDailyPlanRepository
import com.dicoding.hanebado.core.utils.datamapper.DailyPlanMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyPlanRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IDailyPlanRepository {

    override fun addDailyPlan(dailyPlanRequest: DailyPlanRequest): Flow<Resource<DailyPlanDomain>> {
        return object : NetworkBoundResource<DailyPlanDomain, AddDailyPlanResponse>() {
            override suspend fun fetchFromApi(response: AddDailyPlanResponse): DailyPlanDomain {
                // Mengonversi AddDailyPlanResponse ke DailyPlanDomain menggunakan mapper
                return DailyPlanMapper.mapAddDailyPlanResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<AddDailyPlanResponse>> {
                // Menggunakan dailyPlanRequest yang diterima sebagai parameter
                return remoteDataSource.addDailyPlan(dailyPlanRequest)
            }

            override fun onFetchFailed() {
                // Log tambahan atau pengolahan kesalahan lainnya bisa ditambahkan di sini jika diperlukan
            }
        }.asFlow()
    }

    override fun getAllDailyPlan(): Flow<Resource<DailyPlanListDomain>> {
        return object : NetworkBoundResource<DailyPlanListDomain, GetAllDailyResponse>() {
            override suspend fun fetchFromApi(response: GetAllDailyResponse): DailyPlanListDomain {
                return DailyPlanMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<GetAllDailyResponse>> {
                return remoteDataSource.getAllDailyPlan()
            }

            override fun onFetchFailed() {
                Log.e("DailyPlanRepository", "Failed to fetch daily plans")
            }
        }.asFlow()
    }

    override fun getTodayDailyPlan(): Flow<Resource<List<TodayDailyPlanDomain>>> {
        return object : NetworkBoundResource<List<TodayDailyPlanDomain>, GetTodayResponse>() {
            override suspend fun fetchFromApi(response: GetTodayResponse): List<TodayDailyPlanDomain> {
                return DailyPlanMapper.mapTodayResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<GetTodayResponse>> {
                return remoteDataSource.getTodayDailyPlan()
            }

            override fun onFetchFailed() {
                Log.e("DailyPlanRepository", "Failed to fetch today's daily plan")
            }
        }.asFlow()
    }
}