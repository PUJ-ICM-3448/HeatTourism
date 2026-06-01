package com.naranjapina.heat_tourism.data.auth.model

data class AuthUser(
    val authId: String,
    val id: String,
    val email: String,
    val roles: List<UserRole> = listOf(UserRole.TOURIST),
    val fullName: String = "",
    val userName: String = "",
    val phone: String = "",
    val avatarURL: String? = null,

    val nationality: String? = null,
    val city: String = "",
    val country: String = "",
)