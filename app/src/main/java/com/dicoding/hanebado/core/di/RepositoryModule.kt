package com.dicoding.hanebado.core.di

import com.dicoding.hanebado.core.data.repository.AuthRepository
import com.dicoding.hanebado.core.data.repository.DailyPlanRepository
import com.dicoding.hanebado.core.data.repository.ExerciseRepository
import com.dicoding.hanebado.core.data.repository.HistoryRepository
import com.dicoding.hanebado.core.domain.auth.repository.IAuthRepository
import com.dicoding.hanebado.core.domain.dailyplan.repository.IDailyPlanRepository
import com.dicoding.hanebado.core.domain.exercise.repository.IExerciseRepository
import com.dicoding.hanebado.core.domain.histories.repository.IHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Binds
    abstract fun provideDailyPlanRepository(dailyPlanRepository: DailyPlanRepository): IDailyPlanRepository

    @Binds
    abstract fun provideExerciseRepository(exerciseRepository: ExerciseRepository): IExerciseRepository

    @Binds
    abstract fun provideHistoryRepository(historyRepository: HistoryRepository): IHistoryRepository

}