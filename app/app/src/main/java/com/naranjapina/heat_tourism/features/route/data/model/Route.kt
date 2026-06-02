package com.naranjapina.heat_tourism.features.route.data.model

import com.google.firebase.firestore.DocumentId

data class Route(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val duration: String = "",
    val price: String = "",
    val stops: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
