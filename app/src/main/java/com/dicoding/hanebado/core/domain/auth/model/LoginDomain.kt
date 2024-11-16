package com.dicoding.hanebado.core.domain.auth.model

data class LoginDomain(
    val dataDomain: DataDomain,
    val success: Boolean,
    val message: String
)