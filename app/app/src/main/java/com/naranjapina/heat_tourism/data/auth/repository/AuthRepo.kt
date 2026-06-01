package com.naranjapina.heat_tourism.data.auth.repository

import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.data.auth.model.AuthException
import com.naranjapina.heat_tourism.data.auth.model.AuthUser
import com.naranjapina.heat_tourism.data.auth.model.UserRole
import kotlinx.coroutines.tasks.await

class AuthRepo {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun login(email: String, password: String): AuthUser {
        try {
            val res = auth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = res.user

            if (firebaseUser != null) {

                try {
                    val result = db.collection("users")
                        .whereEqualTo("auth_id", firebaseUser.uid)
                        .get()
                        .await()

                    return AuthUser(
                        id = result.documents[0].id,
                        authId = firebaseUser.uid.orEmpty(),
                        email = result.documents[0].get("email").toString(),
                        fullName = result.documents[0].get("fullName").toString(),
                        userName = result.documents[0].get("userName").toString(),
                        phone = result.documents[0].get("phone").toString(),
                        avatarURL = result.documents[0].get("avatarURL").toString(),
                        nationality = result.documents[0].get("nationality").toString(),
                        city = result.documents[0].get("city").toString(),
                        country = result.documents[0].get("country").toString()
                    )

                } catch (e: Exception) {
                    throw AuthException.UnknownAuthException(e.localizedMessage)
                }

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

    suspend fun registerTourist(
        email: String,
        password: String,
        fullName: String,
        userName: String,
        phone: String,
        city: String,
        country: String,
        nationality: String
    ): AuthUser {
        try {
            val req = auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            val firebaseUser = req.user

            if (firebaseUser != null) {

                val payload = hashMapOf(
                    "authId" to firebaseUser.uid,
                    "email" to email,
                    "roles" to listOf(UserRole.TOURIST),
                    "fullName" to fullName,
                    "userName" to userName,
                    "phone" to phone,
                    "nationality" to nationality,
                    "city" to city,
                    "country" to country
                )

                try {
                    val user = db.collection("users")
                        .add(payload)
                        .await()

                    return AuthUser(
                        authId = firebaseUser.uid,
                        id = user.id,
                        email = email,
                        phone = phone,
                        roles = listOf(UserRole.TOURIST),
                        fullName = fullName,
                        userName = userName,
                        nationality = nationality,
                        city = city,
                        country = country
                    )
                } catch (e: Exception) {
                    throw AuthException.UnknownAuthException(e.localizedMessage)
                }

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