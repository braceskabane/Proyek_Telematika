package com.dicoding.hanebado.view.otp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.OtpDomain
import com.dicoding.hanebado.core.domain.auth.model.ResendOtpDomain
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _otpResult = MutableStateFlow<Resource<OtpDomain>>(Resource.Loading())
    val otpResult: StateFlow<Resource<OtpDomain>> = _otpResult.asStateFlow()

    private val _resendOtpResult = MutableStateFlow<Resource<ResendOtpDomain>>(Resource.Loading())
    val resendOtpResult: StateFlow<Resource<ResendOtpDomain>> = _resendOtpResult.asStateFlow()

    fun activateOtp(userId: String, otpCode: String) {
        viewModelScope.launch {
            authUseCase.activateOtp(userId, otpCode)
                .onStart {
                    Log.d("OtpViewModel", "Starting OTP activation")
                    _otpResult.value = Resource.Loading()
                }
                .catch { e ->
                    Log.e("OtpViewModel", "Error caught during activation: $e")
                    val translatedError = translateErrorMessage(e.toString())
                    Log.d("OtpViewModel", "Translated error: $translatedError")
                    _otpResult.value = Resource.Error(translatedError)
                }
                .collect { result ->
                    Log.d("OtpViewModel", "Received activation result: $result")
                    _otpResult.value = when (result) {
                        is Resource.Error -> {
                            val translatedError = translateErrorMessage(result.message ?: "Unknown error")
                            Log.d("OtpViewModel", "Translated error from result: $translatedError")
                            Resource.Error(translatedError)
                        }
                        else -> result
                    }
                }
        }
    }

    fun resendOTP(userId: String) {
        viewModelScope.launch {
            authUseCase.resendOTP(userId)
                .onStart {
                    Log.d("OtpViewModel", "Starting OTP resend")
                    _resendOtpResult.value = Resource.Loading()
                }
                .catch { e ->
                    Log.e("OtpViewModel", "Error caught during resend: $e")
                    val translatedError = translateErrorMessage(e.toString())
                    Log.d("OtpViewModel", "Translated error: $translatedError")
                    _resendOtpResult.value = Resource.Error(translatedError)
                }
                .collect { result ->
                    Log.d("OtpViewModel", "Received resend result: $result")
                    _resendOtpResult.value = when (result) {
                        is Resource.Error -> {
                            val translatedError = translateErrorMessage(result.message ?: "Unknown error")
                            Log.d("OtpViewModel", "Translated error from result: $translatedError")
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

        Log.d("OtpViewModel", "Http Code: $httpCode")

        return when (httpCode) {
            400 -> "Kode OTP tidak valid"
            401 -> "Sesi OTP telah berakhir"
            404 -> "User tidak ditemukan"
            429 -> "Terlalu banyak percobaan, silakan tunggu beberapa saat"
            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti"
            else -> when {
                errorMessage.contains("timeout", ignoreCase = true) -> "Koneksi timeout, silakan coba lagi"
                errorMessage.contains("network", ignoreCase = true) -> "Tidak ada koneksi internet"
                else -> "Terjadi kesalahan: $errorMessage"
            }
        }
    }
}