package com.naranjapina.heat_tourism.shared.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.naranjapina.heat_tourism.data.auth.model.AuthUser
import com.naranjapina.heat_tourism.features.auth.presentation.register.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
  private val auth : FirebaseAuth = Firebase.auth
  private val _state = MutableStateFlow(AuthState())
  val state = _state.asStateFlow()


  fun logOutUser () {
    auth.signOut()
    _state.value = _state.value.copy(
      authUser = null,
      user = null
    )
  }

  fun onUpdateUser(user: AuthUser) {
    _state.value = _state.value.copy(
      authUser = auth.currentUser,
      user = user
    )
  }
}