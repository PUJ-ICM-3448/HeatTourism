package com.naranjapina.heat_tourism.features.company.data.model

data class Company(
    val id: String? = null,
    val name: String = "",
    val companyAvatarURL: String? = null,
    val biography: String? = null,
    val contactEmail: String = "",
    val contactPhone: String = "",

    val activeRoutesIds: List<String> = emptyList(), // IDs de rutas que opera

    val rating: Double = 0.0
)
