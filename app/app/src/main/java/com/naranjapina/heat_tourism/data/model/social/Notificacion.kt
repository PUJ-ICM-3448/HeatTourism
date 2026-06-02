package com.naranjapina.heat_tourism.data.model.social

import com.google.firebase.Timestamp

enum class TipoNotificacion {
    CHAT,
    SOLICITUD_AMISTAD,
    ALERTA_COORDINADOR
}

data class Notificacion(
    val id: String = "",
    val userId: String = "",
    val fromUserId: String? = null,
    val tipo: TipoNotificacion = TipoNotificacion.CHAT,
    val titulo: String = "",
    val cuerpo: String = "",
    val chatId: String? = null,
    val amistadId: String? = null,
    val leida: Boolean = false,
    val createdAt: Timestamp? = null
)

