package com.naranjapina.heat_tourism.data.model

sealed class AuthException : Exception() {
    class InvalidCredentialsException : AuthException()
    class UserNotFoundException : AuthException()
    class NetworkException : AuthException()

    class UnknownAuthException(override val message: String?) : AuthException()
}
