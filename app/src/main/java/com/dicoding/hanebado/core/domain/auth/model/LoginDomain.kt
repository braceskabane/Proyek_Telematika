package com.dicoding.hanebado.core.domain.auth.model

data class LoginDomain(
    val accessToken: String,
    val refreshToken: String,
    val success: Boolean,
    val message: String
)