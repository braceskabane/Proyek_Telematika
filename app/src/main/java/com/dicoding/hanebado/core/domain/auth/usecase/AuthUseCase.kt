package com.dicoding.hanebado.core.domain.auth.usecase

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.domain.auth.model.RegisterDomain
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun login(email: String, password: String): Flow<Resource<LoginDomain>>
    fun register(name: String, email: String, password: String): Flow<Resource<RegisterDomain>>

    // Token
    fun saveAccessToken(token: String): Flow<Boolean>

    fun getAccessToken(): Flow<String>

    suspend fun deleteToken()

    fun saveLoginStatus(isLogin: Boolean): Flow<Boolean>

    fun getLoginStatus(): Flow<Boolean>
}
