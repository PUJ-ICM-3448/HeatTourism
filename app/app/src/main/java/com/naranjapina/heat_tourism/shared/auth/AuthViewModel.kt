package com.naranjapina.heat_tourism.shared.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.data.auth.model.AuthUser
import com.naranjapina.heat_tourism.data.auth.model.UserRole
import com.naranjapina.heat_tourism.features.auth.presentation.register.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
  private val auth : FirebaseAuth = Firebase.auth
  private val db: FirebaseFirestore = Firebase.firestore

  private val _state = MutableStateFlow(AuthState())
  val state = _state.asStateFlow()

  init {
    auth.addAuthStateListener { firebaseAuth ->
      val currentUser = firebaseAuth.currentUser

      Log.d("AuthViewModel", "Current user: $currentUser");

      if (currentUser != null) {
        viewModelScope.launch {
          try {
            val result = db.collection("users")
              .whereEqualTo("authId", currentUser.uid)
              .get()
              .await()

            Log.d("AuthViewModel", "Current document: ${result.documents}");

            if (!result.isEmpty) {
              val document = result.documents[0]

              _state.update { currentState ->
                currentState.copy(
                  authUser = currentUser,
                  user = currentState.user ?: AuthUser(
                    id = document.id,
                    authId = currentUser.uid,
                    email = document.getString("email").orEmpty(),
                    fullName = document.getString("fullName").orEmpty(),
                    userName = document.getString("userName").orEmpty(),
                    phone = document.getString("phone").orEmpty(),
                    avatarURL = document.getString("avatarURL").orEmpty(),
                    nationality = document.getString("nationality").orEmpty(),
                    city = document.getString("city").orEmpty(),
                    country = document.getString("country").orEmpty(),
                    roles = (document.get("roles") as? List<String> ?: emptyList()).map { UserRole.valueOf(it) },
                  ),
                  isLoading = false
                )
              }
            } else {
              _state.update { currentState ->
                currentState.copy(
                  authUser = null,
                  user = null,
                  isLoading = false
                )
              }
            }
          } catch (e: Exception) {
            e.printStackTrace()
            // Importante: aun en caso de error, dejar de cargar para no bloquear el Splash
            _state.update { it.copy(isLoading = false) }
          }
        }
      } else {
        _state.update { currentState ->
          currentState.copy(
            authUser = null,
            user = null,
            isLoading = false
          )
        }
      }
    }
  }

  fun logOutUser () {
    auth.signOut()
    _state.update {
      it.copy(
        authUser = null,
        user = null
      )
    }
  }

  fun onUpdateUser(user: AuthUser) {
    _state.update {
      it.copy(
        authUser = auth.currentUser,
        user = user
      )
    }
  }
}
