package com.dicoding.hanebado.core.data.source.remote

import android.util.Log
import com.dicoding.hanebado.core.data.source.remote.network.ApiResponse
import com.dicoding.hanebado.core.data.source.remote.network.ApiService
import com.dicoding.hanebado.core.data.source.remote.response.ActiveResponse
import com.dicoding.hanebado.core.data.source.remote.response.LoginResponse
import com.dicoding.hanebado.core.data.source.remote.response.OtpResponse
import com.dicoding.hanebado.core.data.source.remote.response.RegisterResponse
import com.dicoding.hanebado.core.data.source.remote.response.ResendOtpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.EOFException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Flow<ApiResponse<LoginResponse>> {
        return flow {
            try {
                Log.d("RemoteDataSource", "Login request: email=$email, password=$password")
                val response = apiService.login(email, password)
                emit(ApiResponse.Success(response))
            } catch (e: HttpException) {
                Log.e("RemoteDataSource", "Login HTTP error: ${e.code()}, ${e.message()}")
                emit(ApiResponse.Error(e))
            } catch (e: Exception) {
                Log.e("RemoteDataSource", "Login error: ${e.javaClass.simpleName}, ${e.message}")
                emit(ApiResponse.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun register(name: String, email: String, password: String): Flow<ApiResponse<RegisterResponse>> {
        return flow {
            try {
                Log.d("RemoteDataSource", "Register request: name=$name, email=$email")
                val response = apiService.register(name, email, password)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e))
                Log.e("RemoteDataSource", "Register error: ${e.toString()}")
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun activateOtp(userId: String, otpCode: String): Flow<ApiResponse<OtpResponse>> = flow {
        try {
            val response = apiService.otp(userId, otpCode)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun resendOTP(userId: String): Flow<ApiResponse<ResendOtpResponse>> = flow {
        try {
            val response = apiService.resendOTP(userId)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun activateCheck(email: String): Flow<ApiResponse<ActiveResponse>> = flow {
        emit(ApiResponse.Loading)

        try {
            Log.d("RemoteDataSource", "Starting activation check for email: $email")

            val response = withContext(Dispatchers.IO) {
                try {
                    apiService.activateCheck(email)
                } catch (e: EOFException) {
                    Log.d("RemoteDataSource", "Received empty response, treating as activated account for email: $email")
                    ActiveResponse(
                        id = "",
                        email = email,
                        isActivated = true
                    )
                }
            }

            when {
                response == null -> {
                    Log.d("RemoteDataSource", "Null response received for email: $email")
                    emit(ApiResponse.Empty)
                }
                response.email == null -> {
                    Log.d("RemoteDataSource", "Response with null email received for: $email")
                    emit(ApiResponse.Empty)
                }
                else -> {
                    Log.d("RemoteDataSource", "Success response for email: $email, isActivated: ${response.isActivated}")
                    emit(ApiResponse.Success(response))
                }
            }

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                404 -> "Account not found"
                401 -> "Unauthorized access"
                500 -> "Server error"
                else -> "Network error: ${e.message()}"
            }
            Log.e("RemoteDataSource", "HTTP error for email: $email, code: ${e.code()}, message: $errorMessage", e)
            emit(ApiResponse.Error(e))

        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is EOFException -> {
                    Log.d("RemoteDataSource", "EOFException caught, treating as activated account for: $email")
                    emit(ApiResponse.Success(
                        ActiveResponse(
                            id = "",
                            email = email,
                            isActivated = true
                        )
                    ))
                }
                else -> {
                    Log.e("RemoteDataSource", "Error during activation check for email: $email", e)
                    emit(ApiResponse.Error(e))
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}
