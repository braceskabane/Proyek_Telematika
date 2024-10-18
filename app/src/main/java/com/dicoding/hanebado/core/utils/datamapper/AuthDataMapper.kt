package com.dicoding.hanebado.core.utils.datamapper

import com.dicoding.hanebado.core.data.source.remote.response.LoginResponse
import com.dicoding.hanebado.core.data.source.remote.response.RegisterResponse
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.domain.auth.model.RegisterDomain
import com.dicoding.hanebado.core.domain.auth.model.UserDomain

object AuthDataMapper {
    fun mapLoginResponseToDomain(response: LoginResponse): LoginDomain {
        return LoginDomain(
            accessToken = response.accessToken ?: "",
            refreshToken = response.refreshToken ?: "",
            success = response.success ?: false,
            message = response.message ?: ""
        )
    }

    fun mapRegisterResponseToDomain(response: RegisterResponse): RegisterDomain {
        return RegisterDomain(
            user = UserDomain(
                id = response.data?.id ?: "",
                name = response.data?.name ?: "",
                email = response.data?.email ?: "",
                isActivated = response.data?.isActivated ?: false,
                createdAt = response.data?.createdAt ?: "",
                updatedAt = response.data?.updatedAt ?: ""
            ),
            success = response.success ?: false,
            message = response.message ?: ""
        )
    }
}