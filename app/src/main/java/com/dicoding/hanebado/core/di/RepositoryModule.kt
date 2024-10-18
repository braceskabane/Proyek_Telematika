package com.dicoding.hanebado.core.di

import com.dicoding.hanebado.core.data.repository.AuthRepository
import com.dicoding.hanebado.core.domain.auth.repository.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(authRepository: AuthRepository): IAuthRepository
}