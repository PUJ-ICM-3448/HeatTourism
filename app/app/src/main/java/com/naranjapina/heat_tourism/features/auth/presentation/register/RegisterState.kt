package com.naranjapina.heat_tourism.features.auth.presentation.register

import com.naranjapina.heat_tourism.data.auth.model.AuthUser

data class RegisterState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,

    // Text Input
    val email: String? = null,
    val fullName: String? = null,
    val userName: String? = null,
    val phone: String? = null,
    val nationality: String? = null,
    val city: String? = null,
    val country: String? = null,

    val password: String? = null,

    val user: AuthUser? = null
)
