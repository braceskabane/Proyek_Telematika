package com.dicoding.hanebado.view.otp

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
                .onStart { _otpResult.value = Resource.Loading() }
                .catch { e ->
                    _otpResult.value = Resource.Error(e.toString())
                }
                .collect { result ->
                    _otpResult.value = result
                }
        }
    }

    fun resendOTP(userId: String) {
        viewModelScope.launch {
            authUseCase.resendOTP(userId)
                .onStart { _resendOtpResult.value = Resource.Loading() }
                .catch { e ->
                    _resendOtpResult.value = Resource.Error(e.toString())
                }
                .collect { result ->
                    _resendOtpResult.value = result
                }
        }
    }
}