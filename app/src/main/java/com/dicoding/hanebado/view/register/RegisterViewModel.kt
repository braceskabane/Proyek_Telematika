package com.dicoding.hanebado.view.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.RegisterDomain
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _registerResult = MutableLiveData<Resource<RegisterDomain>>()

    fun register(name: String, email: String, password: String): LiveData<Resource<RegisterDomain>> {
        viewModelScope.launch {
            Log.d("RegisterViewModel", "Starting registration process")
            authUseCase.register(name, email, password)
                .onStart {
                    Log.d("RegisterViewModel", "Registration started")
                    _registerResult.value = Resource.Loading()
                }
                .catch { e ->
                    Log.e("RegisterViewModel", "Error caught: $e")
                    val translatedError = translateErrorMessage(e.toString())
                    Log.d("RegisterViewModel", "Translated error: $translatedError")
                    _registerResult.value = Resource.Error(translatedError)
                }
                .collect { result ->
                    Log.d("RegisterViewModel", "Received result: $result")
                    _registerResult.value = when (result) {
                        is Resource.Error -> {
                            val translatedError = translateErrorMessage(result.message ?: "Unknown error")
                            Log.d("RegisterViewModel", "Translated error from result: $translatedError")
                            Resource.Error(translatedError)
                        }
                        else -> result
                    }
                }
        }
        return _registerResult
    }

    private fun translateErrorMessage(errorMessage: String): String {
        val httpCode = errorMessage.replace(Regex("[^0-9]"), "")
            .let { if (it.startsWith("2") && it.length > 3) it.substring(1) else it }
            .toIntOrNull()

        Log.d("RegisterViewModel", "Http Code: $httpCode")

        return when (httpCode) {
            400 -> "Data registrasi tidak valid"
            401 -> "Kredensial tidak valid"
            403 -> "Akses ditolak"
            404 -> "Data tidak ditemukan"
            409 -> "Email sudah terdaftar"
            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti"
            else -> when {
                errorMessage.contains("email", ignoreCase = true) -> "Format email tidak valid"
                errorMessage.contains("password", ignoreCase = true) -> "Password minimal 8 karakter"
                else -> "Terjadi kesalahan: $errorMessage"
            }
        }
    }
}