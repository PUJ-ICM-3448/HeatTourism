package com.naranjapina.heat_tourism.data.model

data class MapPoint(
    val id: String,
    val name: String,
    val description: String,
    val longitude: Double,
    val latitude: Double,
    val category: String = "general"
)
