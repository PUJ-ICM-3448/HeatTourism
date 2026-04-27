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

/**
 * Helper para acceso a la localizacion del usuario (Bloque B).
 * Usa FusedLocationProviderClient de Google Play Services.
 */
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

    /**
     * Pide la ubicacion actual del usuario (alta precision).
     * Devuelve null si no hay permisos o si no se pudo obtener una lectura.
     */
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

    /**
     * Reverse geocoding: dado lat/lng devuelve un texto legible
     * tipo "Park Guell, Barcelona". Si falla devuelve null.
     */
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
}
