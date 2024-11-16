package com.dicoding.hanebado.di

import com.dicoding.hanebado.core.domain.auth.interactor.AuthInteractor
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import com.dicoding.hanebado.core.domain.dailyplan.interactor.DailyPlanInteractor
import com.dicoding.hanebado.core.domain.dailyplan.usecase.DailyPlanUseCase
import com.dicoding.hanebado.core.domain.exercise.interactor.ExerciseInteractor
import com.dicoding.hanebado.core.domain.exercise.usecase.ExerciseUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideAuthUseCase(authInteractor: AuthInteractor): AuthUseCase

    @Binds
    @Singleton
    abstract fun provideDailyPlanUseCase(dailyPlanInteractor: DailyPlanInteractor): DailyPlanUseCase

    @Binds
    @Singleton
    abstract fun provideExerciseUseCase(exerciseInteractor: ExerciseInteractor): ExerciseUseCase
}