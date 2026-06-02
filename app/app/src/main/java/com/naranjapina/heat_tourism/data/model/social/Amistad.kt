package com.naranjapina.heat_tourism.data.model.social

import com.google.firebase.Timestamp

enum class EstadoAmistad {
    PENDIENTE,
    ACEPTADA,
    RECHAZADA
}

data class Amistad(
    val id: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val requesterPhotoUrl: String? = null,
    val addresseeId: String = "",
    val addresseeName: String = "",
    val addresseePhotoUrl: String? = null,
    val participantIds: List<String> = emptyList(),
    val estado: EstadoAmistad = EstadoAmistad.PENDIENTE,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

