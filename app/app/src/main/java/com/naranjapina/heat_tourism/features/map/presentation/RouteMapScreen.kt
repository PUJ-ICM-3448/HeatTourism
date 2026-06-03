package com.naranjapina.heat_tourism.features.map.presentation

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.naranjapina.heat_tourism.data.service.LocationTrackingService

@Composable
fun RouteMapScreen(
    groupId: String,
    userId: String,
    isCoordinator: Boolean
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState()
    val usersLocations = remember { mutableStateMapOf<String, LatLng>() }

    // Iniciar el LocationTrackingService cuando se monta la pantalla;
    // detenerlo cuando el usuario sale.
    DisposableEffect(groupId, userId) {
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            putExtra("userId", userId)
            putExtra("groupId", groupId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
        onDispose {
            context.stopService(Intent(context, LocationTrackingService::class.java))
        }
    }

    LaunchedEffect(groupId) {
        FirebaseFirestore.getInstance()
            .collection("groups")
            .document(groupId)
            .collection("locations")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.forEach { doc ->
                    val lat = doc.getDouble("lat") ?: return@forEach
                    val lng = doc.getDouble("lng") ?: return@forEach

                    usersLocations[doc.id] = LatLng(lat, lng)
                }
            }
    }

    val heatmapPoints = usersLocations.values.toList()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        usersLocations.forEach { (id, pos) ->
            if (isCoordinator) {
                Marker(
                    state = MarkerState(position = pos),
                    title = "User: $id"
                )
            } else {
                if (id == userId) {
                    Marker(
                        state = MarkerState(position = pos),
                        title = "Yo"
                    )
                }
            }
        }

        if (isCoordinator && heatmapPoints.isNotEmpty()) {
            val provider = HeatmapTileProvider.Builder()
                .data(heatmapPoints)
                .build()

            TileOverlay(tileProvider = provider)
        }
    }
}
