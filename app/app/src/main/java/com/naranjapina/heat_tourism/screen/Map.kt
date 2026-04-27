package com.naranjapina.heat_tourism.screen

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.data.SampleDestinations
import com.naranjapina.heat_tourism.data.model.MapPoint
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.utils.MapboxConfig
import com.naranjapina.heat_tourism.utils.bottomBorder

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavHostController) {
    MenuBottonLayout(
        activeName = "map", navController = navController
    ) { paddingValues ->
        val context = LocalContext.current

        // Bloque B: configurar token publico de Mapbox antes de inflar MapView
        LaunchedEffect(Unit) {
            val token = MapboxConfig.accessToken(context)
            if (token.isNotBlank()) {
                MapboxOptions.accessToken = token
            }
        }

        val permissionsState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // Pedir permisos en runtime la primera vez que se entra a Mapa
        LaunchedEffect(Unit) {
            if (!permissionsState.allPermissionsGranted) {
                permissionsState.launchMultiplePermissionRequest()
            }
        }

        val hasPermission = permissionsState.permissions.any { it.status.isGranted }
        var selected by remember { mutableStateOf<MapPoint?>(null) }

        Column(modifier = Modifier.fillMaxSize()) {
            Header(paddingValues)

            Box(modifier = Modifier.fillMaxSize()) {
                MapboxBarcelonaMap(
                    destinations = SampleDestinations.barcelonaDestinations,
                    showUserLocation = hasPermission,
                    onMarkerSelected = { selected = it }
                )

                selected?.let { point ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        DestinationCard(
                            point = point,
                            onDismiss = { selected = null },
                            onSeeRoute = {
                                navController.navigate(
                                    "${Screen.RouteOverview.name}?destinationId=${point.id}"
                                )
                                selected = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(paddingValues: PaddingValues) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .bottomBorder(
                strokeWidth = 1.dp,
                color = colorResource(R.color.red_100)
            )
            .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Mapa",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
    }
}

/**
 * Vista de Mapbox embebida en Compose con AndroidView (Bloque B).
 *  - Centra la camara en Barcelona.
 *  - Muestra la capa de ubicacion del usuario si hay permiso.
 *  - Pinta marcadores para cada destino y notifica al tocar uno.
 */
@Composable
private fun MapboxBarcelonaMap(
    destinations: List<MapPoint>,
    showUserLocation: Boolean,
    onMarkerSelected: (MapPoint) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var pointManager by remember { mutableStateOf<PointAnnotationManager?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(2.1734, 41.3851)) // Barcelona
                        .zoom(11.5)
                        .build()
                )
                mapboxMap.loadStyle(Style.STANDARD) {
                    val manager = annotations.createPointAnnotationManager()
                    manager.addClickListener { annotation ->
                        val data = annotation.getData()
                        val id = if (data is com.google.gson.JsonPrimitive) data.asString
                                 else data?.toString()
                        val match = destinations.firstOrNull { it.id == id }
                        if (match != null) onMarkerSelected(match)
                        true
                    }
                    pointManager = manager
                    refreshDestinations(manager, destinations)
                }
            }
        },
        update = { view ->
            view.location.updateSettings {
                enabled = showUserLocation
                pulsingEnabled = showUserLocation
            }
            pointManager?.let { refreshDestinations(it, destinations) }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            pointManager?.deleteAll()
            pointManager = null
        }
    }
}

private fun refreshDestinations(
    manager: PointAnnotationManager,
    destinations: List<MapPoint>
) {
    manager.deleteAll()
    destinations.forEach { p ->
        val opts = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(p.longitude, p.latitude))
            .withTextField(p.name)
            .withTextOffset(listOf(0.0, 1.4))
            .withTextSize(11.0)
            .withData(com.google.gson.JsonPrimitive(p.id))
        manager.create(opts)
    }
}

@Composable
private fun DestinationCard(
    point: MapPoint,
    onDismiss: () -> Unit,
    onSeeRoute: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = colorResource(R.color.red_400)
            )
            Text(
                text = point.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Cerrar",
                color = colorResource(R.color.dark_beige),
                modifier = Modifier.clickable { onDismiss() }
            )
        }
        Text(
            text = point.description,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Ver ruta hasta aqui",
            color = colorResource(R.color.red_400),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable { onSeeRoute() }
        )
    }
}
