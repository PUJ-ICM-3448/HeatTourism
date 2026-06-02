package com.naranjapina.heat_tourism.features.travel.data.model

import com.google.firebase.firestore.DocumentId

data class GrupoViaje(
    @DocumentId val id: String? = null,
    val routeId: String = "",
    val routeName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, ACTIVE, COMPLETED, CANCELLED
    val alert: String? = null,
    val attendanceStarted: Boolean = false
)
