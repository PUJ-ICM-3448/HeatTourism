package com.naranjapina.heat_tourism.core.utils

import com.naranjapina.heat_tourism.core.component.DestinationCardData

/**
 * Datos hardcodeados para la entrega 2 (mono-usuario).
 * Se usan para simular el feed que se "actualiza" con el shake del acelerometro.
 */

val mockDestinations = listOf(
    DestinationCardData(
        destinationName = "Bali, Indonesia",
        destinationScore = 4.78f,
        imgUrl = "https://www.outlooktravelmag.com/media/bali-1-1679062958.profileImage.2x-1536x884.webp",
        contentDescription = "Playas paradisíacas en Bali"
    ),
    DestinationCardData(
        destinationName = "Cartagena, Colombia",
        destinationScore = 4.65f,
        imgUrl = "https://images.unsplash.com/photo-1583531352515-8884af319dc1?w=1200",
        contentDescription = "Ciudad amurallada de Cartagena"
    ),
    DestinationCardData(
        destinationName = "Kioto, Japón",
        destinationScore = 4.82f,
        imgUrl = "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?w=1200",
        contentDescription = "Templos antiguos de Kioto"
    ),
    DestinationCardData(
        destinationName = "Santorini, Grecia",
        destinationScore = 4.91f,
        imgUrl = "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=1200",
        contentDescription = "Casas blancas de Santorini"
    ),
    DestinationCardData(
        destinationName = "Marrakech, Marruecos",
        destinationScore = 4.55f,
        imgUrl = "https://images.unsplash.com/photo-1597211684565-dca64d72bdfe?w=1200",
        contentDescription = "Mercados de Marrakech"
    ),
    DestinationCardData(
        destinationName = "Río de Janeiro, Brasil",
        destinationScore = 4.71f,
        imgUrl = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?w=1200",
        contentDescription = "Vista panorámica de Río"
    ),
    DestinationCardData(
        destinationName = "Reikiavik, Islandia",
        destinationScore = 4.88f,
        imgUrl = "https://images.unsplash.com/photo-1490650034439-fd184c3c86a5?w=1200",
        contentDescription = "Auroras boreales en Islandia"
    ),
    DestinationCardData(
        destinationName = "Petra, Jordania",
        destinationScore = 4.79f,
        imgUrl = "https://images.unsplash.com/photo-1518684079-3c830dcef090?w=1200",
        contentDescription = "Ciudad antigua de Petra"
    )
)

data class MockPublication(
    val imgUrl: String,
    val location: String,
    val autorName: String,
    val age: String,
    val contentDescription: String
)

val mockPublications = listOf(
    MockPublication(
        imgUrl = "https://sagradafamiliatickets.tours/wp-content/uploads/2024/10/sagrada-familia-architecture-3.jpg",
        location = "Sagrada Familia",
        autorName = "Carlos R.",
        age = "15 min",
        contentDescription = "Sagrada Familia"
    ),
    MockPublication(
        imgUrl = "https://images.unsplash.com/photo-1583422409516-2895a77efded?w=800",
        location = "Park Güell",
        autorName = "María L.",
        age = "32 min",
        contentDescription = "Park Güell"
    ),
    MockPublication(
        imgUrl = "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?w=800",
        location = "La Rambla",
        autorName = "Andrés P.",
        age = "1 h",
        contentDescription = "La Rambla"
    ),
    MockPublication(
        imgUrl = "https://images.unsplash.com/photo-1591458890670-be08f4c4cc20?w=800",
        location = "Casa Batlló",
        autorName = "Sofía M.",
        age = "2 h",
        contentDescription = "Casa Batlló"
    ),
    MockPublication(
        imgUrl = "https://images.unsplash.com/photo-1505761671935-60b3a7427bad?w=800",
        location = "Barceloneta",
        autorName = "Jeison V.",
        age = "3 h",
        contentDescription = "Playa Barceloneta"
    ),
    MockPublication(
        imgUrl = "https://images.unsplash.com/photo-1562883676-8c7feb83f09b?w=800",
        location = "Camp Nou",
        autorName = "Tomás G.",
        age = "5 h",
        contentDescription = "Camp Nou"
    )
)
