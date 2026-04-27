package com.naranjapina.heat_tourism.data

import com.naranjapina.heat_tourism.data.model.MapPoint

/**
 * Destinos turisticos de ejemplo para Barcelona.
 * Se usan para poblar el mapa principal y la pantalla de RouteOverview
 * mientras no exista un backend (Bloque B).
 */
object SampleDestinations {
    val barcelonaDestinations: List<MapPoint> = listOf(
        MapPoint(
            id = "sagrada-familia",
            name = "Sagrada Familia",
            description = "Basilica disenada por Antoni Gaudi.",
            longitude = 2.1744,
            latitude = 41.4036,
            category = "monumento"
        ),
        MapPoint(
            id = "park-guell",
            name = "Park Guell",
            description = "Parque modernista con vistas a la ciudad.",
            longitude = 2.1527,
            latitude = 41.4145,
            category = "parque"
        ),
        MapPoint(
            id = "casa-batllo",
            name = "Casa Batllo",
            description = "Edificio modernista de Gaudi en el Passeig de Gracia.",
            longitude = 2.1650,
            latitude = 41.3917,
            category = "monumento"
        ),
        MapPoint(
            id = "la-rambla",
            name = "La Rambla",
            description = "Avenida peatonal en el centro historico.",
            longitude = 2.1734,
            latitude = 41.3818,
            category = "calle"
        ),
        MapPoint(
            id = "barceloneta",
            name = "Playa de la Barceloneta",
            description = "Playa urbana mas conocida de la ciudad.",
            longitude = 2.1925,
            latitude = 41.3784,
            category = "playa"
        ),
        MapPoint(
            id = "camp-nou",
            name = "Camp Nou",
            description = "Estadio del FC Barcelona.",
            longitude = 2.1228,
            latitude = 41.3809,
            category = "deporte"
        ),
        MapPoint(
            id = "montjuic",
            name = "Castillo de Montjuic",
            description = "Fortaleza con vistas panoramicas al puerto.",
            longitude = 2.1657,
            latitude = 41.3633,
            category = "mirador"
        ),
        MapPoint(
            id = "boqueria",
            name = "Mercat de la Boqueria",
            description = "Mercado emblematico en La Rambla.",
            longitude = 2.1716,
            latitude = 41.3818,
            category = "gastronomia"
        )
    )

    fun byId(id: String): MapPoint? = barcelonaDestinations.firstOrNull { it.id == id }
}
