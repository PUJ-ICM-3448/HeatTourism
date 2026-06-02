package com.naranjapina.heat_tourism.data.model

/**
 * Enums compartidos para las funcionalidades multi-usuario de la Entrega 3.
 * Cada bloque (A: chats/amigos/notif, B: ubicacion, C: grupos/comentarios) los consume.
 *
 * IMPORTANTE: Estos enums se serializan/deserializan desde Firestore.
 * No renombrar valores sin migrar la base de datos primero.
 */

/** Estado de una relacion de amistad entre dos usuarios. */
enum class EstadoAmistad {
    PENDIENTE,      // Solicitud enviada, esperando respuesta del destinatario
    ACEPTADA,       // Ambos son amigos y pueden ver posts/rutas mutuos
    RECHAZADA,      // El destinatario rechazo la solicitud
    BLOQUEADO       // Uno bloqueo al otro
}

/** Tipo de notificacion in-app y push (FCM). */
enum class TipoNotificacion {
    MENSAJE_CHAT,
    SOLICITUD_AMISTAD,
    AMISTAD_ACEPTADA,
    ALERTA_GRUPO,           // Emitida por el coordinador (volver al bus, etc.)
    LLAMADO_LISTA,          // Coordinador inicio un llamado a lista
    CHECKIN_APROBADO,
    CHECKIN_RECHAZADO,
    COMENTARIO_POST,
    REACCION_POST,
    GENERAL
}

/** Tipo de alerta grupal que un coordinador puede emitir. */
enum class TipoAlerta {
    VOLVER_AL_BUS,
    CAMBIO_DE_PUNTO,
    REUNION,
    EMERGENCIA,
    GENERAL
}

/** Tipo de reaccion que un usuario puede dar a una publicacion. */
enum class TipoReaccion {
    ME_GUSTA,
    FUEGO,
    AMOR,
    IMPRESIONANTE,
    DIVERTIDO
}

/** Estado de un chat (no del mensaje, sino del hilo completo). */
enum class EstadoChat {
    ACTIVO,
    ARCHIVADO,
    SILENCIADO
}

/** Tipo de mensaje dentro de un chat. */
enum class TipoMensaje {
    TEXTO,
    IMAGEN,
    UBICACION,
    SISTEMA           // Mensajes automaticos: "X se unio al grupo", etc.
}

/** Estado de un check-in solicitado por un turista. */
enum class EstadoCheckIn {
    PENDIENTE,
    APROBADO,
    RECHAZADO
}

/** Estado de una reserva/compra de ruta. */
enum class EstadoReserva {
    PENDIENTE_PAGO,
    CONFIRMADA,
    EN_CURSO,           // El coordinador inicio el viaje
    FINALIZADA,
    CANCELADA
}

/** Sentimiento asociado a un lugar (alimenta el heat map agregado). */
enum class Sentimiento {
    DIVERSION,
    RELAJACION,
    CULTURA,
    AVENTURA,
    GASTRONOMICO,
    NATURALEZA,
    ROMANTICO
}
