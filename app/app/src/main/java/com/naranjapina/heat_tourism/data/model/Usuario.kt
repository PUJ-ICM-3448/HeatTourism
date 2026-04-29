package com.naranjapina.heat_tourism.data.model

data class Usuario(
    val id: String = "",
    val nombreCompleto: String = "",
    val username: String = "",
    val correo: String = "",
    val telefono: String = "",
    val fotoPerfilUrl: String? = null,
    val tipo: TipoUsuario = TipoUsuario.TURISTA,
    val nacionalidad: String? = null,
    val preferenciasViaje: List<PreferenciaViaje> = emptyList(),
    val biografia: String = "",
    val ciudad: String = "",
    val pais: String = "",
    val totalViajes: Int = 0,
    val totalPosts: Int = 0,
    val codigoEmpleado: String? = null,
    val ubicacionCompartidaActiva: Boolean? = null,
    val cargo: String? = null
)
