package com.naranjapina.heat_tourism.features.home.presentation.Home

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.*
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.core.utils.*
import com.naranjapina.heat_tourism.data.network.WeatherRepository
import com.naranjapina.heat_tourism.data.service.LocationTrackingService
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TravelHomeScreen(navController: NavHostController, userId: String) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val ambientTemp = rememberAmbientTemperature()
    var publications by remember { mutableStateOf(mockPublications.shuffled().take(2)) }
    var realTemp by remember { mutableStateOf<Float?>(null) }
    val weatherRepo = remember { WeatherRepository() }

    val backgroundLocationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else null

    LaunchedEffect(Unit) {
        try {
            realTemp = weatherRepo.getTemperature("Barcelona")
        } catch (e: Exception) { e.printStackTrace() }

        // Requisito Bloque B: Iniciar seguimiento con el userId real
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            putExtra("userId", userId)
            putExtra("groupId", "grupo123")
        }
        ContextCompat.startForegroundService(context, intent)

        if (backgroundLocationPermissionState?.status?.isGranted == false) {
            backgroundLocationPermissionState.launchPermissionRequest()
        }
    }

    RememberShakeDetector {
        publications = mockPublications.shuffled().take(2)
        scope.launch { listState.animateScrollToItem(0) }
    }

    MenuBottonLayout(activeName = "home", navController = navController) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.background(colorResource(R.color.beige)).padding(bottom = paddingValues.calculateBottomPadding()).fillMaxSize()
        ) {
            item {
                HomeHeader(paddingValues, title = "Barcelona Explorer", subtitle = "Barcelona, España")
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TemperatureWidget(
                    modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth(),
                    state = ambientTemp,
                    destinationName = "Barcelona",
                    destinationTempC = realTemp ?: 25f
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                CardRow(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    list = listOf(
                        ActionCardGridItem(
                            title = "CheckIn (Live)",
                            painter = R.drawable.map,
                            subtitle = "Ver mapa en vivo",
                            color = R.color.orange_400,
                            onClick = { navController.navigate(Screen.RouteMapLive.name) }
                        ),
                        ActionCardGridItem(title = "Publicar", painter = R.drawable.map, subtitle = null, color = R.color.orange_400),
                        ActionCardGridItem(title = "Chat", painter = R.drawable.map, subtitle = null, color = R.color.red_400)
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TitleAndButton("Próximas paradas", "Ver ruta")
                Text(text = "Aun no hay paradas", modifier = Modifier.padding(15.dp))
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TitleAndButton("Publicaciones del grupo", "Ver todas")
                Crossfade(targetState = publications, label = "pubs") { pubs ->
                    Row(Modifier.padding(15.dp, 0.dp), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                        pubs.forEach { pub ->
                            GroupPublication(
                                modifier = Modifier.height(200.dp).weight(1f),
                                imgUrl = pub.imgUrl, location = pub.location,
                                autorName = pub.autorName, contentDescription = pub.contentDescription, age = pub.age
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
                GradientButton(modifier = Modifier.padding(15.dp).fillMaxWidth(), text = "Cerrar Viaje") {
                    navController.navigate(Screen.Home.name)
                }
            }
        }
    }
}

@Composable
fun HomeHeader(paddingValues: PaddingValues, title: String, subtitle: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.background(redToOrangeGradientBrush()).fillMaxWidth()
            .padding(top = paddingValues.calculateTopPadding()).padding(24.dp)
    ) {
        Text(text = title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.clip(RoundedCornerShape(15.dp)).background(Color.White.copy(0.2f)).padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Progreso del viaje", color = Color.White, modifier = Modifier.weight(1f))
            Text("2/5 puntos", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun NoTravelHomeScreen(navController: NavHostController, userId: String) {
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var destinations by remember { mutableStateOf(mockDestinations.shuffled().take(4)) }

    MenuBottonLayout(activeName = "home", navController = navController) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.background(colorResource(R.color.beige)).padding(bottom = paddingValues.calculateBottomPadding()).fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier.background(redToOrangeGradientBrush()).fillMaxWidth()
                        .padding(top = paddingValues.calculateTopPadding()).padding(24.dp)
                ) {
                    Text("Hola, viajero 👋", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("¿A dónde quieres ir hoy?", color = Color.White)
                    Spacer(modifier = Modifier.height(15.dp))
                    JustInputText(value = text, placeholder = "Buscar destinos...", icon = Icons.Outlined.Search, changeValue = { text = it })
                }
            }
            item { 
                Spacer(modifier = Modifier.height(5.dp))
                LazyFilterChipRow()
                TitleAndButton("Destinos en tendencia", "Ver todos") 
            }
            items(destinations) { dest ->
                DestinationCard(
                    modifier = Modifier.fillMaxWidth().height(300.dp).padding(15.dp, 8.dp),
                    data = dest, navController = navController
                )
            }
            item {
                GradientButton(modifier = Modifier.padding(15.dp).fillMaxWidth(), text = "Simular Viaje Activo") {
                    navController.navigate("${Screen.Home.name}?state=travel")
                }
            }
        }
    }
}
