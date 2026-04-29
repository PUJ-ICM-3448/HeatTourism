package com.naranjapina.heat_tourism.shared.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
  private val auth : FirebaseAuth = Firebase.auth

  private val _currentUser = MutableStateFlow(auth.currentUser)
  val currentUser : StateFlow<FirebaseUser?> = _currentUser

  var email by mutableStateOf("")
    private set
  var password by mutableStateOf("")
    private set
  var isLoading by mutableStateOf(false)
    private set
  var feedbackMessage by mutableStateOf<String?>(null)
    private set


  fun sigInUser(
    email: String,
    password: String
  ) {
    if(email.isBlank() || password.isBlank()) {
      feedbackMessage = "Email y contraseña son requeridos"
      return;
    }

    isLoading = true;
    feedbackMessage = null;
    auth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener { task ->
        isLoading = false;
        _currentUser.value = null;
        if(task.isSuccessful) {
          _currentUser.value = auth.currentUser
        } else {
          feedbackMessage = task.exception?.message
        }
      }
  }

  fun logOutUser () {
    auth.signOut()
    _currentUser.value = null

    email = ""
    password = ""
    feedbackMessage = "Sesión cerrada"
  }

  fun signUpUser(
    email: String,
    password: String
  ) {
    if(email.isBlank() || password.isBlank()) {
      feedbackMessage = "Email y contraseña son requeridos"
      return;
    }

    if(password.length < 8) {
      feedbackMessage = "La contraseña debe tener al menos 8 caracteres"
      return;
    }

    isLoading = true
    feedbackMessage = null

    auth.createUserWithEmailAndPassword(
      email,
      password
    )
      .addOnCompleteListener { task ->
        isLoading = false;

        if (task.isSuccessful)
          _currentUser.value = auth.currentUser
        else
          feedbackMessage = task.exception?.message
      }

  }
}