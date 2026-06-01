package com.naranjapina.heat_tourism.features.auth.domain.usecase

import com.naranjapina.heat_tourism.data.auth.model.AuthUser
import com.naranjapina.heat_tourism.data.auth.repository.AuthRepo


class RegisterTouristUseCase(private val repo: AuthRepo = AuthRepo()) {
  suspend operator fun invoke(
    email: String,
    password: String,
    fullName: String,
    userName: String,
    phone: String,
    city: String,
    country: String,
    nationality: String
  ): AuthUser {
    if(
      email.isBlank() ||
      userName.isBlank() ||
      fullName.isBlank() ||
      userName.isBlank() ||
      phone.isBlank() ||
      nationality.isBlank() ||
      city.isBlank() ||
      password.isBlank() ||
      country.isBlank()) throw IllegalArgumentException("Campos vacios")
    if(!email.contains("@")) throw IllegalArgumentException("Email invalido");
    if(password.length < 6) throw IllegalArgumentException("Contraseña muy corta");

    return repo.registerTourist(
      email = email,
      password = password,
      fullName = fullName,
      userName = userName,
      phone = phone,
      city = city,
      country = country,
      nationality = nationality
    );
  }
}