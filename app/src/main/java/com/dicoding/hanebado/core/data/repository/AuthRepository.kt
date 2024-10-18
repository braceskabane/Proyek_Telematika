package com.dicoding.hanebado.core.data.repository

import com.dicoding.hanebado.core.data.source.NetworkBoundResource
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.data.source.local.datastore.DataStoreManager
import com.dicoding.hanebado.core.data.source.remote.RemoteDataSource
import com.dicoding.hanebado.core.data.source.remote.network.ApiResponse
import com.dicoding.hanebado.core.data.source.remote.response.LoginResponse
import com.dicoding.hanebado.core.data.source.remote.response.RegisterResponse
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.domain.auth.model.RegisterDomain
import com.dicoding.hanebado.core.domain.auth.repository.IAuthRepository
import com.dicoding.hanebado.core.utils.datamapper.AuthDataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val dataStoreManager: DataStoreManager
) : IAuthRepository {
    override fun login(email: String, password: String): Flow<Resource<LoginDomain>> {
        return object : NetworkBoundResource<LoginDomain, LoginResponse>() {
            override suspend fun fetchFromApi(response: LoginResponse): LoginDomain {
                return AuthDataMapper.mapLoginResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<LoginResponse>> {
                return remoteDataSource.login(email, password)
            }
        }.asFlow()
    }

    override fun register(name: String, email: String, password: String): Flow<Resource<RegisterDomain>> {
        return object : NetworkBoundResource<RegisterDomain, RegisterResponse>() {
            override suspend fun fetchFromApi(response: RegisterResponse): RegisterDomain {
                return AuthDataMapper.mapRegisterResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<RegisterResponse>> {
                return remoteDataSource.register(name, email, password)
            }
        }.asFlow()
    }

    override fun saveAccessToken(token: String): Flow<Boolean> {
        return dataStoreManager.saveAccessToken(token)
    }

    override fun getAccessToken(): Flow<String> {
        return dataStoreManager.getAccessToken()
    }

    override suspend fun deleteToken() {
        return dataStoreManager.deleteToken()
    }

    override fun saveLoginStatus(isLogin: Boolean): Flow<Boolean> {
        return dataStoreManager.saveLoginStatus(isLogin)
    }

    override fun getLoginStatus(): Flow<Boolean> {
        return dataStoreManager.getLoginStatus().flowOn(Dispatchers.IO)
    }
}