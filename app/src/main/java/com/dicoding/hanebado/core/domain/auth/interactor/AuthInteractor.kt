package com.dicoding.hanebado.core.domain.auth.interactor

import com.dicoding.hanebado.core.data.repository.AuthRepository
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.domain.auth.model.RegisterDomain
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractor @Inject constructor(private val authRepository: AuthRepository) : AuthUseCase {
    override fun login(email: String, password: String): Flow<Resource<LoginDomain>> {
        return authRepository.login(email, password)
    }

    override fun register(name: String, email: String, password: String): Flow<Resource<RegisterDomain>> {
        return authRepository.register(name, email, password)
    }

    override fun saveAccessToken(token: String): Flow<Boolean> {
        return authRepository.saveAccessToken(token)
    }

    override fun getAccessToken(): Flow<String> {
        return authRepository.getAccessToken()
    }

    override suspend fun deleteToken() {
        authRepository.deleteToken()
    }

    override fun saveLoginStatus(isLogin: Boolean): Flow<Boolean> {
        return authRepository.saveLoginStatus(isLogin)
    }

    override fun getLoginStatus(): Flow<Boolean> {
        return authRepository.getLoginStatus()
    }
}