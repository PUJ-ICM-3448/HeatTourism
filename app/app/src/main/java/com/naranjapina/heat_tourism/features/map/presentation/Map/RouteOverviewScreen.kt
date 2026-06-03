package com.naranjapina.heat_tourism.features.map.presentation.map

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.*
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
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.locationcomponent.location
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.component.LocationPicker
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.core.utils.LocationUtils
import com.naranjapina.heat_tourism.core.utils.MapboxConfig
import com.naranjapina.heat_tourism.data.SampleDestinations
import com.naranjapina.heat_tourism.data.network.CountryRepository
import com.naranjapina.heat_tourism.data.network.CountryResponse
import com.naranjapina.heat_tourism.data.network.WeatherRepository
import com.naranjapina.heat_tourism.data.service.MapboxDirectionsService
import com.naranjapina.heat_tourism.features.map.presentation.model.MapPoint
import com.naranjapina.heat_tourism.features.map.presentation.model.RouteSummary

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RouteOverviewScreen(
    navController: NavHostController,
    destinationId: String? = null
) {
    val context = LocalContext.current
    val weatherRepo = remember { WeatherRepository() }
    val countryRepo = remember { CountryRepository() }

    var destinationTemp by remember { mutableStateOf<Float?>(null) }
    var countryInfo by remember { mutableStateOf<CountryResponse?>(null) }

    val destination = remember(destinationId) {
        destinationId?.let { SampleDestinations.byId(it) }
            ?: SampleDestinations.bogotaDestinations.first()
    }

    // Llamadas REST dinámicas basadas en el destino (REST #1 y #3)
    LaunchedEffect(destination.id) {
        try {
            destinationTemp = weatherRepo.getTemperature(destination.name)
            // Asumimos Colombia para los destinos de ejemplo, pero la info es real de la API
            countryInfo = countryRepo.getCountry("Colombia")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var origin by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var summary by remember { mutableStateOf<RouteSummary?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(permissionsState.allPermissionsGranted, destination.id) {
        loading = true
        val originPair = LocationUtils.getCurrentOrFallbackPoint(context, SampleDestinations.BOGOTA_CENTER_LNG, SampleDestinations.BOGOTA_CENTER_LAT)
        origin = originPair

        val token = MapboxConfig.accessToken(context)
        if (token.isNotBlank()) {
            MapboxOptions.accessToken = token
            val api = MapboxDirectionsService(token)
            api.getRoute(originPair.first, originPair.second, destination.longitude, destination.latitude, "walking")
                .onSuccess { summary = it }
                .onFailure { errorMsg = "Error al calcular ruta" }
            api.close()
        }
        loading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RouteMap(origin, destination, summary, permissionsState.permissions.any { it.status.isGranted })

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.statusBarsPadding().padding(12.dp).background(Color.White.copy(0.9f), RoundedCornerShape(50))
        ) {
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "Volver")
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(15.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(destination.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text(
                        text = countryInfo?.let { "${it.name.common} • ${it.region}" } ?: "Cargando info país...",
                        color = colorResource(R.color.dark_beige), fontSize = 14.sp
                    )
                }
                destinationTemp?.let {
                    Text("${it.toInt()}°C", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = colorResource(R.color.red_400))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (summary != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Stat(label = "Distancia", value = summary!!.formatDistance())
                    Stat(label = "A pie", value = summary!!.formatDuration())
                    countryInfo?.let { Stat(label = "Población", value = "${it.population / 1000000}M") }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            GradientButton(modifier = Modifier.fillMaxWidth(), text = "Iniciar ruta") {
                navController.navigate(Screen.RouteMapLive.name)
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = colorResource(R.color.dark_beige), fontSize = 12.sp)
    }
}

@Composable
private fun RouteMap(origin: Pair<Double, Double>?, destination: MapPoint, summary: RouteSummary?, showUserLocation: Boolean) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var pointManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var polyManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                mapboxMap.loadStyle(Style.STANDARD) {
                    pointManager = annotations.createPointAnnotationManager()
                    polyManager = annotations.createPolylineAnnotationManager()
                }
            }
        },
        update = { view ->
            view.location.updateSettings { enabled = showUserLocation }
            val pm = pointManager ?: return@AndroidView
            val ply = polyManager ?: return@AndroidView
            pm.deleteAll()
            pm.create(PointAnnotationOptions().withPoint(Point.fromLngLat(destination.longitude, destination.latitude)).withTextField(destination.name))
            
            summary?.geometry?.let { geo ->
                val line = LineString.fromLngLats(geo.map { Point.fromLngLat(it.first, it.second) })
                ply.deleteAll()
                ply.create(PolylineAnnotationOptions().withGeometry(line).withLineColor("#E63946").withLineWidth(5.0))
            }
        }
    )
}
