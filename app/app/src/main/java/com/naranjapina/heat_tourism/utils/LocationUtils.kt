package com.naranjapina.heat_tourism.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

object LocationUtils {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }
        }
    }

    suspend fun getCurrentOrFallbackPoint(
        context: Context,
        fallbackLng: Double,
        fallbackLat: Double,
        maxDistanceKm: Double = 80.0
    ): Pair<Double, Double> {
        val loc = getCurrentLocation(context)
        if (loc == null) return fallbackLng to fallbackLat

        if (loc.hasAccuracy() && loc.accuracy > 2000f) {
            return fallbackLng to fallbackLat
        }

        val distanceKm = haversineDistanceKm(
            lat1 = loc.latitude,
            lon1 = loc.longitude,
            lat2 = fallbackLat,
            lon2 = fallbackLng
        )
        return if (distanceKm <= maxDistanceKm) {
            loc.longitude to loc.latitude
        } else {
            fallbackLng to fallbackLat
        }
    }

    suspend fun reverseGeocode(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {
        runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val list = geocoder.getFromLocation(latitude, longitude, 1)
            list?.firstOrNull()?.let { addr ->
                val feature = addr.featureName
                    ?.takeIf { it.isNotBlank() && !it.matches(Regex("^[0-9.\\- ]+$")) }
                val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                listOfNotNull(feature, locality).joinToString(", ").ifBlank { null }
            }
        }.getOrNull()
    }

    private fun haversineDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
            kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return earthRadiusKm * c
    }
}
