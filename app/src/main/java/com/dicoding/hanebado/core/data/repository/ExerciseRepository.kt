package com.dicoding.hanebado.core.data.repository

import com.dicoding.hanebado.core.data.source.NetworkBoundResource
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.data.source.remote.RemoteDataSource
import com.dicoding.hanebado.core.data.source.remote.network.ApiResponse
import com.dicoding.hanebado.core.data.source.remote.response.exercise.ExerciseResponse
import com.dicoding.hanebado.core.domain.exercise.model.Exercise
import com.dicoding.hanebado.core.domain.exercise.repository.IExerciseRepository
import com.dicoding.hanebado.core.utils.datamapper.ExerciseMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IExerciseRepository {

    override fun getAllExercises(): Flow<Resource<List<Exercise>>> {
        return object : NetworkBoundResource<List<Exercise>, ExerciseResponse>() {
            override suspend fun fetchFromApi(response: ExerciseResponse): List<Exercise> {
                return ExerciseMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<ExerciseResponse>> {
                return remoteDataSource.getAllExercises()
            }

            override fun onFetchFailed() {
                // Handle failure if needed
            }
        }.asFlow()
    }
}