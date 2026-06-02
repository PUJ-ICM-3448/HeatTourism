package com.naranjapina.heat_tourism.features.map.presentation.model

data class RouteSummary(
    val distanceMeters: Double,
    val durationSeconds: Double,
    val geometry: List<Pair<Double, Double>>
) {
    val distanceKm: Double get() = distanceMeters / 1000.0
    val durationMinutes: Double get() = durationSeconds / 60.0

    fun formatDistance(): String =
        if (distanceMeters < 1000) "${distanceMeters.toInt()} m"
        else "%.1f km".format(distanceKm)

    fun formatDuration(): String {
        val minutes = durationMinutes.toInt()
        return when {
            minutes < 60 -> "$minutes min"
            else -> {
                val h = minutes / 60
                val m = minutes % 60
                if (m == 0) "${h} h" else "${h} h ${m} min"
            }
        }
    }
}