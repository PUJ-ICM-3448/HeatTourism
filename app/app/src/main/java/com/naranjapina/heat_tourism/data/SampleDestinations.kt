package com.naranjapina.heat_tourism.data

import com.naranjapina.heat_tourism.data.model.MapPoint

object SampleDestinations {
    const val BOGOTA_CENTER_LNG = -74.08175
    const val BOGOTA_CENTER_LAT = 4.60971

    val bogotaDestinations: List<MapPoint> = listOf(
        MapPoint(
            id = "monserrate",
            name = "Monserrate",
            description = "Santuario y mirador iconico de Bogota.",
            longitude = -74.0553,
            latitude = 4.6054,
            category = "monumento"
        ),
        MapPoint(
            id = "museo-oro",
            name = "Museo del Oro",
            description = "Coleccion prehispanica emblematic de Colombia.",
            longitude = -74.0721,
            latitude = 4.6019,
            category = "parque"
        ),
        MapPoint(
            id = "plaza-bolivar",
            name = "Plaza de Bolivar",
            description = "Plaza historica principal del centro de Bogota.",
            longitude = -74.0761,
            latitude = 4.5981,
            category = "monumento"
        ),
        MapPoint(
            id = "la-candelaria",
            name = "La Candelaria",
            description = "Barrio historico con arquitectura colonial.",
            longitude = -74.0716,
            latitude = 4.5968,
            category = "calle"
        ),
        MapPoint(
            id = "parque-93",
            name = "Parque de la 93",
            description = "Zona gastronomica y de ocio en el norte de la ciudad.",
            longitude = -74.0474,
            latitude = 4.6762,
            category = "gastronomia"
        ),
        MapPoint(
            id = "jardin-botanico",
            name = "Jardin Botanico",
            description = "Jardin con colecciones de flora andina.",
            longitude = -74.1026,
            latitude = 4.6630,
            category = "deporte"
        ),
        MapPoint(
            id = "simon-bolivar",
            name = "Parque Simon Bolivar",
            description = "Parque metropolitano y pulmon verde de Bogota.",
            longitude = -74.0939,
            latitude = 4.6581,
            category = "mirador"
        ),
        MapPoint(
            id = "usaquen",
            name = "Usaquen",
            description = "Barrio tradicional con mercado artesanal y restaurantes.",
            longitude = -74.0314,
            latitude = 4.6953,
            category = "gastronomia"
        )
    )

    const val ALL_CATEGORIES = "all"

    private val categoryLabels: Map<String, String> = mapOf(
        ALL_CATEGORIES to "Todas",
        "monumento" to "Monumentos",
        "parque" to "Parques",
        "calle" to "Barrios",
        "gastronomia" to "Gastronomia",
        "deporte" to "Naturaleza",
        "mirador" to "Miradores"
    )

    fun categoryOptions(): List<String> =
        listOf(ALL_CATEGORIES) + bogotaDestinations
            .map { it.category }
            .distinct()
            .sorted()

    fun categoryLabel(categoryId: String): String =
        categoryLabels[categoryId] ?: categoryId.replaceFirstChar { it.uppercase() }

    fun filterByCategory(
        destinations: List<MapPoint>,
        selectedCategory: String?
    ): List<MapPoint> {
        val normalizedCategory = selectedCategory ?: ALL_CATEGORIES
        if (normalizedCategory == ALL_CATEGORIES) return destinations
        return destinations.filter { it.category == normalizedCategory }
    }


    fun byId(id: String): MapPoint? = bogotaDestinations.firstOrNull { it.id == id }
}
