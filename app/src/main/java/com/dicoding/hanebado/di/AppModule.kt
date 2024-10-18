package com.dicoding.hanebado.di

import com.dicoding.hanebado.core.domain.auth.interactor.AuthInteractor
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
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
}