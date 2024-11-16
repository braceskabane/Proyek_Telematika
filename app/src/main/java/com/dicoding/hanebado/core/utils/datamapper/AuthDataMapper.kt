package com.dicoding.hanebado.core.utils.datamapper

import com.dicoding.hanebado.core.data.source.remote.response.auth.ActiveResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.hanebado.core.data.source.remote.response.auth.ResendOtpResponse
import com.dicoding.hanebado.core.domain.auth.model.ActiveCheckDomain
import com.dicoding.hanebado.core.domain.auth.model.DataDomain
import com.dicoding.hanebado.core.domain.auth.model.LoginDomain
import com.dicoding.hanebado.core.domain.auth.model.OtpDomain
import com.dicoding.hanebado.core.domain.auth.model.RegisterDomain
import com.dicoding.hanebado.core.domain.auth.model.ResendOtpDomain
import com.dicoding.hanebado.core.domain.auth.model.User

object AuthDataMapper {
    fun mapLoginResponseToDomain(response: LoginResponse): LoginDomain {
        return LoginDomain(
            dataDomain = DataDomain(
                user = User(
                    id = response.data?.user?.id ?: "",
                    name = response.data?.user?.name ?: "",
                    email = response.data?.user?.email ?: "",
                    isActivated = response.data?.user?.isActivated ?: false,
                    createdAt = response.data?.user?.createdAt ?: "",
                    updatedAt = response.data?.user?.updatedAt ?: ""
                ),
                token = response.data?.token ?: ""
            ),
            success = response.success ?: false,
            message = "" // Jika tidak ada message di LoginResponse, berikan string kosong
        )
    }

    fun mapRegisterResponseToDomain(response: RegisterResponse): RegisterDomain {
        return RegisterDomain(
            dataDomain = DataDomain(
                user = User(
                    id = response.data?.user?.id ?: "",
                    name = response.data?.user?.name ?: "",
                    email = response.data?.user?.email ?: "",
                    isActivated = response.data?.user?.isActivated ?: false,
                    createdAt = response.data?.user?.createdAt ?: "",
                    updatedAt = response.data?.user?.updatedAt ?: ""
                ),
                token = response.data?.token ?: ""
            ),
            success = response.success ?: false,
            message = ""
        )
    }
    fun mapOtpResponseToDomain(response: OtpResponse): OtpDomain {
        return OtpDomain(
            message = response.message ?: ""
        )
    }

    fun ResendOtpResponse.toDomain() = ResendOtpDomain(message)

    fun ActiveResponse.toDomain(): ActiveCheckDomain {
        return ActiveCheckDomain(
            id = id ?: "",
            email = email ?: "",
            name = name ?: "",
            isActivated = isActivated ?: false,
            createdAt = createdAt ?: "",
            updatedAt = updatedAt ?: ""
        )
    }

}