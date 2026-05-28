package com.naranjapina.heat_tourism.data.repository

import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.data.model.AuthException
import kotlinx.coroutines.tasks.await

class AuthRepo {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun login(email: String, password: String): FirebaseUser {
        try {
            val res = auth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = res.user

            if (firebaseUser != null) {

                return firebaseUser
            } else {
                throw AuthException.UnknownAuthException("Firebase retorno un usuario nulo")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException.InvalidCredentialsException()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw AuthException.UserNotFoundException()
        } catch (e: FirebaseNetworkException) {
            throw AuthException.NetworkException()
        } catch (e: Exception) {
            throw AuthException.UnknownAuthException(e.localizedMessage)
        }
    }

    suspend fun register(
        email: String,
        password: String
    ): FirebaseUser {
        try {
            val req = auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            val firebaseUser = req.user

            if (firebaseUser != null) {
                return firebaseUser
            } else {
                throw AuthException.UnknownAuthException("Firebase retorno un usuario nulo")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException.InvalidCredentialsException()
        } catch (e: FirebaseNetworkException) {
            throw AuthException.NetworkException()
        } catch (e: Exception) {
            throw AuthException.UnknownAuthException(e.localizedMessage)
        }
    }
}