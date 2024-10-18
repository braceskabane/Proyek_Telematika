package com.dicoding.hanebado.view.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel() {
    private val _loginResult = MutableStateFlow<Resource<LoginDomain>>(Resource.Loading())
    val loginResult: StateFlow<Resource<LoginDomain>> = _loginResult

    fun login(email: String, password: String) =
        authUseCase.login(email, password).asLiveData()

    private fun saveAccessToken(token: String) = authUseCase.saveAccessToken(token).asLiveData()

    fun getAccessToken() = authUseCase.getAccessToken().asLiveData()

    fun saveLoginStatus(isLogin: Boolean) = authUseCase.saveLoginStatus(isLogin).asLiveData()
    
    fun executeValidateToken(accessToken: String): MediatorLiveData<String> =
        MediatorLiveData<String>().apply {
            addSource(saveAccessToken(accessToken)) { accessTokenSaved ->
                if (accessTokenSaved) {
                    addSource(getAccessToken()) { token ->
                        value = token
                    }
                }
            }
        }
}