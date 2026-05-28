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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.LazyFilterChipRow
import com.naranjapina.heat_tourism.data.SampleDestinations
import com.naranjapina.heat_tourism.data.model.MapPoint
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.utils.LocationUtils
import com.naranjapina.heat_tourism.utils.MapboxConfig
import com.naranjapina.heat_tourism.utils.bottomBorder

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavHostController) {
    MenuBottonLayout(
        activeName = "map", navController = navController
    ) { paddingValues ->
        val context = LocalContext.current
        val mapToken = remember { MapboxConfig.accessToken(context) }

        LaunchedEffect(mapToken) {
            if (mapToken.isNotBlank()) {
                MapboxOptions.accessToken = mapToken
            }
        }

        val permissionsState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        LaunchedEffect(Unit) {
            if (!permissionsState.allPermissionsGranted) {
                permissionsState.launchMultiplePermissionRequest()
            }
        }

        val hasPermission = permissionsState.permissions.any { it.status.isGranted }
        var selected by remember { mutableStateOf<MapPoint?>(null) }
        var selectedCategory by remember { mutableStateOf(SampleDestinations.ALL_CATEGORIES) }
        val categoryOptions = remember { SampleDestinations.categoryOptions() }
        val filteredDestinations = remember(selectedCategory) {
            SampleDestinations.filterByCategory(
                destinations = SampleDestinations.bogotaDestinations,
                selectedCategory = selectedCategory
            )
        }

        LaunchedEffect(filteredDestinations, selected) {
            if (selected != null && filteredDestinations.none { it.id == selected?.id }) {
                selected = null
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                paddingValues = paddingValues,
                onSearchClick = { navController.navigate(Screen.Searcher.name) }
            )

            LazyFilterChipRow(
                chips = categoryOptions.map { SampleDestinations.categoryLabel(it) },
                selectedChip = SampleDestinations.categoryLabel(selectedCategory),
                onChipSelected = { label ->
                    selectedCategory = categoryOptions.firstOrNull {
                        SampleDestinations.categoryLabel(it) == label
                    } ?: SampleDestinations.ALL_CATEGORIES
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (mapToken.isBlank()) {
                    TokenErrorOverlay()
                } else {
                    MapboxBarcelonaMap(
                        destinations = filteredDestinations,
                        showUserLocation = hasPermission,
                        onMarkerSelected = { selected = it },
                        onDestinationLongPressed = { selected = it },
                        onMapTapped = { selected = null }
                    )
                }

                selected?.let { point ->
                    Box(
                        modifier = Modifier
                          .align(Alignment.BottomCenter)
                          .padding(bottom = paddingValues.calculateBottomPadding() + 8.dp)
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
private fun TokenErrorOverlay() {
    Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "El token de Mapbox no es valido. Usa un token publico que empiece por pk. en strings.xml.",
            color = colorResource(R.color.red_500),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun Header(
    paddingValues: PaddingValues,
    onSearchClick: () -> Unit
) {
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
        Text(
            text = "Buscar destino",
            color = colorResource(R.color.red_400),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onSearchClick() }
        )
    }
}

@Composable
private fun MapboxBarcelonaMap(
    destinations: List<MapPoint>,
    showUserLocation: Boolean,
    onMarkerSelected: (MapPoint) -> Unit,
    onDestinationLongPressed: (MapPoint) -> Unit,
    onMapTapped: () -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var destinationManager by remember { mutableStateOf<CircleAnnotationManager?>(null) }
    var userLocationManager by remember { mutableStateOf<CircleAnnotationManager?>(null) }
    var longClickListener by remember { mutableStateOf<OnMapLongClickListener?>(null) }
    var mapClickListener by remember { mutableStateOf<OnMapClickListener?>(null) }
    var userLocationPoint by remember { mutableStateOf<Point?>(null) }

    LaunchedEffect(showUserLocation) {
        if (!showUserLocation) {
            userLocationPoint = null
            return@LaunchedEffect
        }
        val normalized = LocationUtils.getCurrentOrFallbackPoint(
            context = context,
            fallbackLng = SampleDestinations.BOGOTA_CENTER_LNG,
            fallbackLat = SampleDestinations.BOGOTA_CENTER_LAT
        )
        userLocationPoint = Point.fromLngLat(normalized.first, normalized.second)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.apply {
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(
                                Point.fromLngLat(
                                    SampleDestinations.BOGOTA_CENTER_LNG,
                                    SampleDestinations.BOGOTA_CENTER_LAT
                                )
                            )
                            .zoom(11.5)
                            .build()
                    )
                    mapboxMap.loadStyle(Style.STANDARD) {
                        val markerManager = annotations.createCircleAnnotationManager()
                        markerManager.addClickListener { annotation ->
                            val data = annotation.getData()
                            val id = if (data is com.google.gson.JsonPrimitive) data.asString
                            else data?.toString()
                            val match = destinations.firstOrNull { it.id == id }
                            if (match != null) onMarkerSelected(match)
                            true
                        }
                        destinationManager = markerManager
                        userLocationManager = annotations.createCircleAnnotationManager()
                        refreshDestinations(markerManager, destinations)
                        refreshUserLocation(userLocationManager, userLocationPoint)
                    }

                    val longPress = OnMapLongClickListener { pressedPoint ->
                        nearestDestination(
                            destinations,
                            pressedPoint
                        )?.let(onDestinationLongPressed)
                        true
                    }
                    gestures.addOnMapLongClickListener(longPress)
                    longClickListener = longPress

                    val tap = OnMapClickListener {
                        onMapTapped()
                        false
                    }
                    gestures.addOnMapClickListener(tap)
                    mapClickListener = tap
                }
            },
            update = { view ->
                view.location.updateSettings {
                    enabled = showUserLocation
                    pulsingEnabled = showUserLocation
                    locationPuck = createDefault2DPuck(withBearing = true)
                    puckBearing = PuckBearing.COURSE
                    puckBearingEnabled = true
                    pulsingColor = "#2F80ED".toColorInt()
                }
                destinationManager?.let { refreshDestinations(it, destinations) }
                refreshUserLocation(userLocationManager, userLocationPoint)
            }
        )

        Column(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(end = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MapControlButton(icon = Icons.Outlined.Add) {
                zoomMap(mapView, delta = 1.0)
            }
            MapControlTextButton(label = "-") {
                zoomMap(mapView, delta = -1.0)
            }
            MapControlTextButton(label = "Mi") {
                val center = userLocationPoint ?: Point.fromLngLat(
                    SampleDestinations.BOGOTA_CENTER_LNG,
                    SampleDestinations.BOGOTA_CENTER_LAT
                )
                recenterMap(mapView, center)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            longClickListener?.let { mapView.gestures.removeOnMapLongClickListener(it) }
            longClickListener = null
            mapClickListener?.let { mapView.gestures.removeOnMapClickListener(it) }
            mapClickListener = null
            destinationManager?.deleteAll()
            destinationManager = null
            userLocationManager?.deleteAll()
            userLocationManager = null
        }
    }
}

private fun refreshDestinations(
    manager: CircleAnnotationManager,
    destinations: List<MapPoint>
) {
    manager.deleteAll()
    destinations.forEach { p ->
        val opts = CircleAnnotationOptions()
            .withPoint(Point.fromLngLat(p.longitude, p.latitude))
            .withCircleRadius(7.5)
            .withCircleColor("#E63946")
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#FFFFFF")
            .withData(com.google.gson.JsonPrimitive(p.id))
        manager.create(opts)
    }
}

private fun refreshUserLocation(
    manager: CircleAnnotationManager?,
    userPoint: Point?
) {
    val safeManager = manager ?: return
    safeManager.deleteAll()
    userPoint ?: return
    val userCircle = CircleAnnotationOptions()
        .withPoint(userPoint)
        .withCircleRadius(9.0)
        .withCircleColor("#2F80ED")
        .withCircleStrokeColor("#FFFFFF")
        .withCircleStrokeWidth(3.0)
    safeManager.create(userCircle)
}

private fun zoomMap(mapView: MapView, delta: Double) {
    val current = mapView.mapboxMap.cameraState
    mapView.mapboxMap.setCamera(
        CameraOptions.Builder()
            .center(current.center)
            .zoom((current.zoom + delta).coerceIn(3.0, 20.0))
            .build()
    )
}

private fun recenterMap(mapView: MapView, center: Point) {
    mapView.mapboxMap.setCamera(
        CameraOptions.Builder()
            .center(center)
            .zoom(14.0)
            .build()
    )
}

@Composable
private fun MapControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color.White.copy(alpha = 0.92f))
          .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(R.color.red_400)
        )
    }
}

@Composable
private fun MapControlTextButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color.White.copy(alpha = 0.92f))
          .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = colorResource(R.color.red_400),
            fontWeight = FontWeight.Bold
        )
    }
}

private fun nearestDestination(
    destinations: List<MapPoint>,
    pressedPoint: Point
): MapPoint? {
    return destinations.minByOrNull { destination ->
        haversineDistanceMeters(
            lat1 = pressedPoint.latitude(),
            lon1 = pressedPoint.longitude(),
            lat2 = destination.latitude,
            lon2 = destination.longitude
        )
    }
}

private fun haversineDistanceMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val earthRadius = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
        kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
        kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return earthRadius * c
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
