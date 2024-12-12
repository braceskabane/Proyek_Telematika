package com.dicoding.hanebado.view.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow<Boolean>(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            authUseCase.getAccessToken().collect { token ->
                Log.d("MainViewModel", "Current token: ${token.take(10)}...")
                if (token.isNotEmpty()) {
                    authUseCase.getLoginStatus().collect { isLoggedIn ->
                        Log.d("MainViewModel", "Login status: $isLoggedIn")
                        _isAuthenticated.value = isLoggedIn && token.isNotEmpty()
                        Log.d("MainViewModel", "Final auth status: ${_isAuthenticated.value}")
                    }
                } else {
                    _isAuthenticated.value = false
                }
            }
        }
    }

    fun refreshAuthStatus() {
        checkAuthenticationStatus()
    }
}