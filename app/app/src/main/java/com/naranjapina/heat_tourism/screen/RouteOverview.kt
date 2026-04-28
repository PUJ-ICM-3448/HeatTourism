package com.naranjapina.heat_tourism.screen

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.component.LocationPicker
import com.naranjapina.heat_tourism.data.MapboxDirectionsApi
import com.naranjapina.heat_tourism.data.SampleDestinations
import com.naranjapina.heat_tourism.data.model.MapPoint
import com.naranjapina.heat_tourism.data.model.RouteSummary
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.utils.LocationUtils
import com.naranjapina.heat_tourism.utils.MapboxConfig

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RouteOverviewScreen(
    navController: NavHostController,
    destinationId: String? = null
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val token = MapboxConfig.accessToken(context)
        if (token.isNotBlank()) {
            MapboxOptions.accessToken = token
        }
    }


    val destination = remember(destinationId) {
        destinationId?.let { SampleDestinations.byId(it) }
            ?: SampleDestinations.bogotaDestinations.first()
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

    var origin by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var summary by remember { mutableStateOf<RouteSummary?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(permissionsState.allPermissionsGranted, destination.id) {
        loading = true
        errorMsg = null
        val originPair = LocationUtils.getCurrentOrFallbackPoint(
            context = context,
            fallbackLng = SampleDestinations.BOGOTA_CENTER_LNG,
            fallbackLat = SampleDestinations.BOGOTA_CENTER_LAT
        )
        origin = originPair

        val token = MapboxConfig.accessToken(context)
        if (token.isBlank()) {
            errorMsg = "Falta configurar el token de Mapbox en strings.xml"
            loading = false
            return@LaunchedEffect
        }

        val api = MapboxDirectionsApi(token)
        api.getRoute(
            startLng = originPair.first,
            startLat = originPair.second,
            endLng = destination.longitude,
            endLat = destination.latitude,
            profile = "walking"
        ).onSuccess {
            summary = it
        }.onFailure {
            errorMsg = "No se pudo calcular la ruta: ${it.message}"
        }
        api.close()
        loading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RouteMap(
            origin = origin,
            destination = destination,
            summary = summary,
            showUserLocation = permissionsState.permissions.any { it.status.isGranted }
        )

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
            contentDescription = "Volver",
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = .9f))
                .clickable {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.Searcher.name)
                    }
                }
                .padding(12.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(15.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text(
                text = destination.name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = destination.description,
                color = colorResource(R.color.dark_beige)
            )
            Spacer(modifier = Modifier.height(12.dp))

            LocationPicker(
                modifier = Modifier.fillMaxWidth(),
                fallbackLabel = "Plaza de Bolivar, Bogota",
                onChangeRequested = { navController.navigate(Screen.Map.name) }
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Elegir en mapa",
                    color = colorResource(R.color.red_400),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { navController.navigate(Screen.Map.name) }
                )
                Text(
                    text = "Buscar destino",
                    color = colorResource(R.color.red_400),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { navController.navigate(Screen.Searcher.name) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            when {
                loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(R.color.red_400),
                            modifier = Modifier.height(20.dp)
                        )
                        Text("Calculando ruta…")
                    }
                }
                errorMsg != null -> {
                    Text(
                        text = errorMsg!!,
                        color = colorResource(R.color.red_500)
                    )
                }
                summary != null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Stat(label = "Distancia", value = summary!!.formatDistance())
                        Stat(label = "A pie", value = summary!!.formatDuration())
                        Stat(label = "Paradas", value = "1")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Iniciar ruta"
            ) { }
        }
    }
}


@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = label,
            color = colorResource(R.color.dark_beige),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun RouteMap(
    origin: Pair<Double, Double>?,
    destination: MapPoint,
    summary: RouteSummary?,
    showUserLocation: Boolean
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var pointManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var polyManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(destination.longitude, destination.latitude))
                        .zoom(13.0)
                        .build()
                )
                mapboxMap.loadStyle(Style.STANDARD) {
                    pointManager = annotations.createPointAnnotationManager()
                    polyManager = annotations.createPolylineAnnotationManager()
                }
            }
        },
        update = { view ->
            view.location.updateSettings {
                enabled = showUserLocation
                pulsingEnabled = showUserLocation
            }

            val pm = pointManager ?: return@AndroidView
            val ply = polyManager ?: return@AndroidView

            pm.deleteAll()
            ply.deleteAll()

            pm.create(
                PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(destination.longitude, destination.latitude))
                    .withTextField(destination.name)
                    .withTextOffset(listOf(0.0, 1.4))
                    .withTextSize(11.0)
            )
            origin?.let { (lng, lat) ->
                pm.create(
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(lng, lat))
                        .withTextField("Tu ubicacion")
                        .withTextOffset(listOf(0.0, 1.4))
                        .withTextSize(11.0)
                )
            }

            summary?.geometry?.takeIf { it.size >= 2 }?.let { geo ->
                val line = LineString.fromLngLats(
                    geo.map { Point.fromLngLat(it.first, it.second) }
                )
                ply.create(
                    PolylineAnnotationOptions()
                        .withGeometry(line)
                        .withLineColor("#E63946")
                        .withLineWidth(5.0)
                )
                val pts = geo.map { Point.fromLngLat(it.first, it.second) }
                @Suppress("DEPRECATION")
                val cam = view.mapboxMap.cameraForCoordinates(
                    pts,
                    EdgeInsets(120.0, 60.0, 320.0, 60.0),
                    null,
                    null
                )
                view.mapboxMap.setCamera(cam)
            }
        }
    )
}
