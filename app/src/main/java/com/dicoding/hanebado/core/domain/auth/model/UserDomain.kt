package com.dicoding.hanebado.core.domain.auth.model

data class UserDomain(
    val id: String,
    val name: String,
    val email: String,
    val isActivated: Boolean,
    val createdAt: String,
    val updatedAt: String
)