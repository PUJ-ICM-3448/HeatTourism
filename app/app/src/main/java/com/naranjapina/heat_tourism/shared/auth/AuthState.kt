package com.naranjapina.heat_tourism.features.auth.presentation.register

import com.google.firebase.auth.FirebaseUser
import com.naranjapina.heat_tourism.data.auth.model.AuthUser

data class AuthState(
    val user: AuthUser? = null,
    val authUser: FirebaseUser? = null,
    val isLoading: Boolean = true
)
