package com.dicoding.hanebado.core.domain.auth.model

data class RegisterDomain(
    val user: UserDomain,
    val success: Boolean,
    val message: String
)