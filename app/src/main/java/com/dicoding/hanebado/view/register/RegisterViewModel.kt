package com.dicoding.hanebado.view.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.model.RegisterDomain
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _registerResult = MutableStateFlow<Resource<RegisterDomain>>(Resource.Loading())
    val registerResult: StateFlow<Resource<RegisterDomain>> = _registerResult

    fun register(name: String, email: String, password: String) =
        authUseCase.register(name, email, password).asLiveData()

}