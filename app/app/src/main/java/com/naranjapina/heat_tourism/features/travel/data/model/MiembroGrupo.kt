package com.naranjapina.heat_tourism.features.travel.data.model

import com.google.firebase.firestore.DocumentId

data class MiembroGrupo(
    @DocumentId val id: String? = null, // Matches the user's authId/id
    val fullName: String = "",
    val role: String = "TOURIST", // TOURIST, COORDINATOR, ADMINISTRATOR
    val checkInStatus: String = "PENDING", // PENDING, APPROVED, REJECTED
    val isPresent: Boolean = false
)
