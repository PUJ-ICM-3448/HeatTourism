package com.naranjapina.heat_tourism.features.map.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.HeatmapTileProvider

@Composable
fun RouteMapScreen(
    groupId: String,
    userId: String,
    isCoordinator: Boolean
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState()
    val usersLocations = remember { mutableStateMapOf<String, LatLng>() }

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
