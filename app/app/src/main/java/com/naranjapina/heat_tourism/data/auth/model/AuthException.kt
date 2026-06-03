package com.naranjapina.heat_tourism.data.auth.model

sealed class AuthException : Exception() {
    class InvalidCredentialsException : AuthException()
    class UserNotFoundException : AuthException()
    class NetworkException : AuthException()

    class UnknownAuthException(override val message: String?) : AuthException()
}