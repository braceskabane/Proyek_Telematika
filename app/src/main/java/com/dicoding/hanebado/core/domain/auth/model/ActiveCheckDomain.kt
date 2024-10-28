package com.dicoding.hanebado.core.domain.auth.model

data class ActiveCheckDomain(
    val id: String,
    val email: String,
    val name: String,
    val isActivated: Boolean,
    val createdAt: String,
    val updatedAt: String
)