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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
  private val auth : FirebaseAuth = Firebase.auth
  private val firestore = Firebase.firestore

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
          val current = auth.currentUser
          if (current != null) {
            upsertUserProfile(
              uid = current.uid,
              email = current.email.orEmpty(),
              displayName = current.displayName.orEmpty().ifBlank {
                current.email?.substringBefore("@") ?: "Usuario"
              },
              photoUrl = current.photoUrl?.toString()
            )
            syncFcmToken(current.uid)
          }
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
    fullName: String,
    email: String,
    password: String
  ) {
    if(fullName.isBlank() || email.isBlank() || password.isBlank()) {
      feedbackMessage = "Nombre, email y contraseña son requeridos"
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

        if (task.isSuccessful) {
          auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
              .setDisplayName(fullName.trim())
              .build()
          )
          _currentUser.value = auth.currentUser
          val current = auth.currentUser
          if (current != null) {
            upsertUserProfile(
              uid = current.uid,
              email = current.email.orEmpty(),
              displayName = fullName.trim(),
              photoUrl = current.photoUrl?.toString()
            )
            syncFcmToken(current.uid)
          }
        } else
          feedbackMessage = task.exception?.message
      }

  }

  private fun upsertUserProfile(
    uid: String,
    email: String,
    displayName: String,
    photoUrl: String?
  ) {
    firestore.collection("users")
      .document(uid)
      .set(
        mapOf(
          "uid" to uid,
          "email" to email,
          "searchEmail" to email.lowercase(),
          "displayName" to displayName,
          "searchName" to displayName.lowercase(),
          "photoUrl" to photoUrl,
          "updatedAt" to FieldValue.serverTimestamp(),
          "createdAt" to FieldValue.serverTimestamp()
        ),
        com.google.firebase.firestore.SetOptions.merge()
      )
  }

  private fun syncFcmToken(uid: String) {
    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
      firestore.collection("users")
        .document(uid)
        .set(
          mapOf(
            "fcmTokens" to FieldValue.arrayUnion(token),
            "updatedAt" to FieldValue.serverTimestamp()
          ),
          com.google.firebase.firestore.SetOptions.merge()
        )
    }
  }
}
