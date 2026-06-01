package com.naranjapina.heat_tourism.features.auth.presentation.register

import com.naranjapina.heat_tourism.data.auth.model.AuthUser

data class RegisterState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,

    val email: String = "",
    val fullName: String = "",
    val userName: String = "",
    val phone: String = "",
    val nationality: String = "",
    val city: String = "",
    val country: String = "",
    val password: String = "",

    val user: AuthUser? = null
)