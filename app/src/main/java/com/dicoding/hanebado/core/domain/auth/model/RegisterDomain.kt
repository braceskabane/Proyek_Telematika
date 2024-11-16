package com.dicoding.hanebado.core.domain.auth.model

data class RegisterDomain(
    val dataDomain: DataDomain,
    val success: Boolean,
    val message: String
)