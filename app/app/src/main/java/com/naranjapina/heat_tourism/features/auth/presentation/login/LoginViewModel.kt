package com.naranjapina.heat_tourism.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.data.auth.model.AuthException
import com.naranjapina.heat_tourism.features.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel (
    private val loginUseCase: LoginUseCase = LoginUseCase()
): ViewModel(
) {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }
    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onLoginEvent() {
        val currentEmail = _state.value.email
        val currentPassword = _state.value.password

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = loginUseCase(
                    email = currentEmail,
                    password = currentPassword
                )
                _state.update { it.copy(isLoading = false, isAuthenticated = true, user = user) }
            } catch (e: AuthException) {
                val errorMessage = when (e) {
                    is AuthException.InvalidCredentialsException -> "Correo o contraseña incorrectos."
                    is AuthException.UserNotFoundException -> "Este usuario no está registrado."
                    is AuthException.NetworkException -> "No hay conexión a internet."
                    is AuthException.UnknownAuthException -> e.message ?: "Error inesperado."
                }
                _state.update { it.copy(isLoading = false, error = errorMessage) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }
}
