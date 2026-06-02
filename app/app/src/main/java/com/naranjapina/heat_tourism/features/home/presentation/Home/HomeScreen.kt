package com.naranjapina.heat_tourism.features.home.presentation.Home

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.DestinationCard
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.component.JustInputText
import com.naranjapina.heat_tourism.core.component.TitleAndButton
import com.naranjapina.heat_tourism.core.component.GroupPublication
import com.naranjapina.heat_tourism.core.component.LazyFilterChipRow
import com.naranjapina.heat_tourism.core.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.core.utils.RememberShakeDetector
import com.naranjapina.heat_tourism.core.utils.redToOrangeGradientBrush
import com.naranjapina.heat_tourism.core.utils.rememberAmbientTemperature
import kotlinx.coroutines.launch

const val COORDINATOR = "COORDINATOR"

@Composable
fun HomeDispatcher(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val activeGroupId by homeViewModel.activeGroupId.collectAsStateWithLifecycle()

    if (activeGroupId != null) {
        HomeWithTravelScreen(navController, homeViewModel)
    } else {
        NoTravelHomeScreen(navController, homeViewModel)
    }
}

@Composable
fun NoTravelHomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val destinations by homeViewModel.destinations.collectAsStateWithLifecycle()

    RememberShakeDetector {
        homeViewModel.loadHomeData()
        scope.launch {
            listState.animateScrollToItem(0)
        }
        Toast.makeText(context, "Feed actualizado 🔄", Toast.LENGTH_SHORT).show()
    }

    MenuBottonLayout(activeName = "home", navController = navController) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(color = colorResource(R.color.beige))
                .padding(0.dp, 0.dp, 0.dp, paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .background(brush = redToOrangeGradientBrush())
                        .fillMaxWidth()
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Hola, viajero 👋",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¿A dónde quieres ir hoy?",
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    JustInputText(
                        value = text,
                        placeholder = "Buscar destinos, lugares, eventos...",
                        icon = Icons.Outlined.Search,
                        changeValue = { text = it }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(5.dp))
                LazyFilterChipRow()
            }

            item {
                TitleAndButton("Destinos en tendencia", "Ver todos")
            }

            items(destinations, key = { it.destinationName }) { destination ->
                DestinationCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                        .animateItem(
                            fadeInSpec = tween(durationMillis = 500),
                            fadeOutSpec = tween(durationMillis = 300),
                            placementSpec = tween(durationMillis = 500)
                        ),
                    data = destination,
                    navController = navController
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

@Composable
fun HomeWithTravelScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val ambientTempState = rememberAmbientTemperature()
    val user by homeViewModel.user.collectAsStateWithLifecycle()
    val publications by homeViewModel.publications.collectAsStateWithLifecycle()
    val activeGroup by homeViewModel.activeGroup.collectAsStateWithLifecycle()
    val activeGroupId by homeViewModel.activeGroupId.collectAsStateWithLifecycle()
    val members by homeViewModel.members.collectAsStateWithLifecycle()

    LaunchedEffect(activeGroupId) {
        activeGroupId?.let { id ->
            homeViewModel.listenForAlerts(context, id)
        }
    }

    RememberShakeDetector {
        homeViewModel.loadHomeData()
        scope.launch {
            listState.animateScrollToItem(0)
        }
        Toast.makeText(context, "Publicaciones del grupo actualizadas 🔄", Toast.LENGTH_SHORT).show()
    }

    MenuBottonLayout(activeName = "home", navController = navController) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(color = colorResource(R.color.beige))
                .padding(0.dp, 0.dp, 0.dp, paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .background(brush = redToOrangeGradientBrush())
                        .fillMaxWidth()
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
                        .padding(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Viaje activo",
                                color = Color.LightGray,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = activeGroup?.routeName ?: "Cargando viaje...",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = when(activeGroup?.status) {
                                "ACTIVE" -> "En curso"
                                "PENDING" -> "Pendiente"
                                "COMPLETED" -> "Finalizado"
                                else -> "Cargando..."
                            },
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .background(colorResource(R.color.neutral_100).copy(alpha = 0.25f))
                                .padding(10.dp),
                            fontSize = 14.sp,
                        )
                    }
                    Text(
                        text = "Ruta: ${activeGroup?.routeName ?: "---"}",
                        color = Color.White,
                    )

                    // Coordinator Controls
                    if (user.tipo == COORDINATOR) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GradientButton(
                                modifier = Modifier.weight(1f),
                                text = "Emitir Alerta"
                            ) {
                                activeGroupId?.let { id ->
                                    homeViewModel.emitAlert(id, "¡Atención! Reunión en el punto de control.")
                                }
                            }
                            GradientButton(
                                modifier = Modifier.weight(1f),
                                text = "Iniciar Lista"
                            ) {
                                activeGroupId?.let { id ->
                                    homeViewModel.startAttendance(id)
                                }
                            }
                        }
                    }

                    if (user.tipo == COORDINATOR) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .background(colorResource(R.color.green_400).copy(alpha = 0.25f))
                                .padding(15.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Panel de Control",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (activeGroup?.attendanceStarted == true) {
                                Text("Llamado a lista activo", color = Color.White, fontSize = 12.sp)
                                members.forEach { member ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(member.fullName ?: "Sin nombre", color = Color.White)
                                        Checkbox(
                                            checked = member.isPresent == true,
                                            onCheckedChange = { isChecked ->
                                                activeGroupId?.let { gid ->
                                                    member.id?.let { uid ->
                                                        homeViewModel.markPresence(gid, uid, isChecked)
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                GradientButton(text = "Finalizar Lista") {
                                    activeGroupId?.let { id ->
                                        homeViewModel.stopAttendance(id)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White)
                        .padding(15.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Temperatura ambiente", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "${ambientTempState.temperature?.toInt() ?: "--"}°C",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    GradientButton(text = "Ver mapa", modifier = Modifier.height(40.dp)) {
                        navController.navigate(Screen.Map.name)
                    }
                }
            }

            item {
                TitleAndButton("Muro del grupo", "Ver todo")
            }

            items(publications) { post ->
                GroupPublication(
                    imgUrl = post.imageUrl ?: "",
                    autorName = post.userName ?: "Anónimo",
                    location = post.location ?: "Ubicación desconocida",
                    age = "Reciente",
                    contentDescription = post.description ?: "",
                    onClick = {
                        navController.navigate("${Screen.Post.name}?postId=${post.id}")
                    },
                    modifier = Modifier.padding(15.dp).fillMaxWidth().height(250.dp)
                )
            }
        }
    }
}
