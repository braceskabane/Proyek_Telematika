package com.dicoding.hanebado.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.ActiveCheckDomain
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel() {
    private val _loginResult = MutableStateFlow<Resource<LoginDomain>>(Resource.Loading())
    val loginResult: StateFlow<Resource<LoginDomain>> = _loginResult

    private val _activationState = MutableStateFlow<Resource<ActiveCheckDomain>>(Resource.Loading())
    val activationState = _activationState.asStateFlow()

    private var currentActivationJob: Job? = null

    fun checkActivation(email: String) {
        // Cancel previous job
        currentActivationJob?.cancel()

        currentActivationJob = viewModelScope.launch(Dispatchers.IO) {  // Tambahkan Dispatchers.IO
            try {
                Log.d("LoginViewModel", "Starting activation check for: $email")
                authUseCase.activateCheck(email)
                    .collect { result ->
                        Log.d("LoginViewModel", "Received result: $result")
                        _activationState.value = result
                    }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("LoginViewModel", "Error during activation check", e)
                    _activationState.value = Resource.Error(e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentActivationJob?.cancel()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Starting login process")
            authUseCase.login(email, password)
                .onStart {
                    Log.d("LoginViewModel", "Login started")
                    _loginResult.value = Resource.Loading()
                }
                .catch { e ->
                    Log.e("LoginViewModel", "Error caught: $e")
                    val translatedError = translateErrorMessage(e.toString())
                    Log.d("LoginViewModel", "Translated error: $translatedError")
                    _loginResult.value = Resource.Error(translatedError)
                }
                .collect { result ->
                    Log.d("LoginViewModel", "Received result: $result")
                    _loginResult.value = when (result) {
                        is Resource.Error -> {
                            val translatedError = translateErrorMessage(result.message ?: "Unknown error")
                            Log.d("LoginViewModel", "Translated error from result: $translatedError")
                            Resource.Error(translatedError)
                        }
                        else -> result
                    }
                }
        }
    }

    private fun translateErrorMessage(errorMessage: String): String {
        val httpCode = errorMessage.replace(Regex("[^0-9]"), "")
            .let { if (it.startsWith("2") && it.length > 3) it.substring(1) else it }
            .toIntOrNull()

        Log.d("LoginViewModel", "Http Code: $httpCode")

        return when (httpCode) {
            401 -> "Kredensial tidak valid atau akun tidak terdaftar"
            403 -> "Akses ditolak. Anda tidak memiliki izin untuk melakukan tindakan ini"
            404 -> "Data tidak ditemukan"
            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti"
            else -> "Terjadi kesalahan: $errorMessage"
        }
    }

    fun saveAccessToken(token: String) = authUseCase.saveAccessToken(token).asLiveData()

    fun saveLoginStatus(isLogin: Boolean) = authUseCase.saveLoginStatus(isLogin).asLiveData()
}