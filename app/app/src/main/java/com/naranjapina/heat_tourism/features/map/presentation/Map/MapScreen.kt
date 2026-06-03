package com.naranjapina.heat_tourism.features.map.presentation.Map

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonPrimitive
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.heatmapLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.LazyFilterChipRow
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.core.utils.MapboxConfig
import com.naranjapina.heat_tourism.core.utils.bottomBorder
import com.naranjapina.heat_tourism.data.SampleDestinations
import com.naranjapina.heat_tourism.features.map.presentation.model.MapPoint
import kotlin.math.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavHostController) {
    val heatPoints = remember { mutableStateListOf<Point>() }
    
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("groups")
            .document("grupo123")
            .collection("locations")
            .addSnapshotListener { snapshot, _ ->
                heatPoints.clear()
                snapshot?.documents?.forEach { doc ->
                    val lat = doc.getDouble("lat") ?: return@forEach
                    val lng = doc.getDouble("lng") ?: return@forEach
                    heatPoints.add(Point.fromLngLat(lng, lat))
                }
            }
    }

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
                    MapboxMapContent(
                        destinations = filteredDestinations,
                        showUserLocation = hasPermission,
                        heatPoints = heatPoints,
                        onMarkerSelected = { selected = it },
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
private fun MapboxMapContent(
    destinations: List<MapPoint>,
    showUserLocation: Boolean,
    heatPoints: List<Point>,
    onMarkerSelected: (MapPoint) -> Unit,
    onMapTapped: () -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var destinationManager by remember { mutableStateOf<CircleAnnotationManager?>(null) }

    LaunchedEffect(heatPoints.size) {
        mapView.mapboxMap.getStyle { style ->
            val source = style.getSourceAs<com.mapbox.maps.extension.style.sources.generated.GeoJsonSource>("heatmap-source")
            source?.featureCollection(FeatureCollection.fromFeatures(heatPoints.map { Feature.fromGeometry(it) }))
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(SampleDestinations.BOGOTA_CENTER_LNG, SampleDestinations.BOGOTA_CENTER_LAT))
                        .zoom(11.5)
                        .build()
                )
                mapboxMap.loadStyle(Style.DARK) { style ->
                    val markerManager = annotations.createCircleAnnotationManager()
                    markerManager.addClickListener { annotation ->
                        val data = annotation.getData()
                        val id = if (data is JsonPrimitive) data.asString else data?.toString()
                        destinations.firstOrNull { it.id == id }?.let(onMarkerSelected)
                        true
                    }
                    destinationManager = markerManager
                    
                    style.addSource(geoJsonSource("heatmap-source") {
                        featureCollection(FeatureCollection.fromFeatures(heatPoints.map { Feature.fromGeometry(it) }))
                    })
                    style.addLayer(heatmapLayer("heatmap-layer", "heatmap-source") {
                        heatmapOpacity(0.8)
                        heatmapRadius(20.0)
                    })
                }
                gestures.addOnMapClickListener { onMapTapped(); false }
            }
        },
        update = { view ->
            view.location.updateSettings { enabled = showUserLocation }
            destinationManager?.let { manager ->
                manager.deleteAll()
                destinations.forEach { p ->
                    manager.create(CircleAnnotationOptions()
                        .withPoint(Point.fromLngLat(p.longitude, p.latitude))
                        .withCircleRadius(7.0).withCircleColor("#E63946")
                        .withData(JsonPrimitive(p.id)))
                }
            }
        }
    )
}

@Composable
private fun DestinationCard(point: MapPoint, onDismiss: () -> Unit, onSeeRoute: () -> Unit) {
    Column(modifier = Modifier.padding(15.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.LocationOn, null, tint = colorResource(R.color.red_400))
            Spacer(Modifier.width(8.dp))
            Text(point.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Text("Cerrar", color = colorResource(R.color.dark_beige), modifier = Modifier.clickable { onDismiss() })
        }
        Text(point.description, modifier = Modifier.padding(top = 8.dp))
        Text("Ver ruta hasta aquí", color = colorResource(R.color.red_400), fontWeight = FontWeight.SemiBold, 
            modifier = Modifier.padding(top = 12.dp).clickable { onSeeRoute() })
    }
}

@Composable
private fun TokenErrorOverlay() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("Configure el token de Mapbox en strings.xml", color = colorResource(R.color.red_500), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Header(paddingValues: PaddingValues, onSearchClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(Color.White).bottomBorder(1.dp, colorResource(R.color.red_100))
        .padding(top = paddingValues.calculateTopPadding()).padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Mapa", fontWeight = FontWeight.Bold, fontSize = 28.sp)
        Text("Buscar", color = colorResource(R.color.red_400), fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onSearchClick() })
    }
}
