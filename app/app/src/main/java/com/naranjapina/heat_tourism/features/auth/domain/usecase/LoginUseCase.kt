package com.naranjapina.heat_tourism.features.auth.domain.usecase

import com.naranjapina.heat_tourism.data.auth.model.AuthUser
import com.naranjapina.heat_tourism.data.auth.repository.AuthRepo


class LoginUseCase(private val repo: AuthRepo = AuthRepo()) {
  suspend operator fun invoke(
    email: String,
    password: String,
  ): AuthUser {
    if(
      email.isBlank() ||
      password.isBlank()) throw IllegalArgumentException("Campos vacios")
    if(!email.contains("@")) throw IllegalArgumentException("Email invalido");
    if(password.length < 6) throw IllegalArgumentException("Contraseña muy corta");

    return repo.login(email, password);
  }
}