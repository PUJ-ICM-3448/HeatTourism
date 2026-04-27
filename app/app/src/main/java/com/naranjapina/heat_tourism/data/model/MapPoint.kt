package com.naranjapina.heat_tourism.data.model

/**
 * Punto turistico mostrado en el mapa (Bloque B).
 * Las coordenadas estan en grados decimales (WGS84).
 */
data class MapPoint(
    val id: String,
    val name: String,
    val description: String,
    val longitude: Double,
    val latitude: Double,
    val category: String = "general"
)
