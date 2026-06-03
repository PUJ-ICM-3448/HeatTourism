package com.naranjapina.heat_tourism.core.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*

class LocationProvider(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10_000 // cada 10 segundos
    ).apply {
        setMinUpdateIntervalMillis(5_000)
    }.build()

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onLocation: (lat: Double, lng: Double) -> Unit) {

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    onLocation(location.latitude, location.longitude)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            context.mainLooper
        )
    }

    fun stopLocationUpdates(callback: LocationCallback) {
        fusedLocationClient.removeLocationUpdates(callback)
    }
}