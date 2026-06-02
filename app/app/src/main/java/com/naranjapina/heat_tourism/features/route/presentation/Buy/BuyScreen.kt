package com.naranjapina.heat_tourism.features.route.presentation.Buy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyScreen(
    navController: NavHostController,
    routeId: String? = null,
    authViewModel: AuthViewModel = viewModel(),
    buyViewModel: BuyViewModel = viewModel()
) {
    val authState by authViewModel.state.collectAsState()
    val buyState by buyViewModel.state.collectAsState()
    val routeData by buyViewModel.route.collectAsState()

    LaunchedEffect(routeId) {
        routeId?.let { buyViewModel.loadRoute(it) }
    }

    val currentRouteId = routeId ?: ""
    val routeName = routeData?.name ?: "Cargando ruta..."
    val routePrice = routeData?.price ?: "--"

    val user = authState.user

    Scaffold(
        containerColor = colorResource(R.color.beige)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = buyState) {
                is BuyState.Idle -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                                    contentDescription = "Volver",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { navController.popBackStack() }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Confirmar Compra",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = Color.Black
                                )
                            }

                            // Route Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "DETALLES DE LA RUTA",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = colorResource(R.color.red_400)
                                    )
                                    Text(
                                        text = routeName,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                    HorizontalDivider(color = colorResource(R.color.beige), thickness = 1.dp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Precio Total", color = Color.Gray, fontSize = 14.sp)
                                        Text(
                                            text = routePrice,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = colorResource(R.color.red_600)
                                        )
                                    }
                                }
                            }

                            // Payment Method Card (Mock)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "MÉTODO DE PAGO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = colorResource(R.color.red_400)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Tarjeta de Crédito (Mock)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "•••• 4242",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = "Los fondos se debitarán inmediatamente para reservar tu cupo y crear el grupo de viaje.",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // Bottom Actions
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (user == null) {
                                Text(
                                    text = "Debes iniciar sesión para unirte a una ruta.",
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                            GradientButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Confirmar Pago",
                                enabled = user != null
                            ) {
                                if (user != null) {
                                    buyViewModel.purchaseRoute(currentRouteId, routeName, user)
                                }
                            }
                        }
                    }
                }

                is BuyState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(R.color.red_400),
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Procesando pago y creando tu grupo...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }
                }

                is BuyState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¡Compra Exitosa!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Se ha creado tu Grupo de Viaje en Firestore y estás registrado como Miembro del Grupo.",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 22.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        GradientButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Ir a mi Viaje"
                        ) {
                            buyViewModel.resetState()
                            // Navigate to TravelHomeScreen by passing state = "travel"
                            navController.navigate("${Screen.Home.name}?state=travel") {
                                popUpTo(Screen.Home.name) { inclusive = true }
                            }
                        }
                    }
                }

                is BuyState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Ocurrió un problema",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    buyViewModel.resetState()
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                            GradientButton(
                                modifier = Modifier.weight(1f),
                                text = "Reintentar"
                            ) {
                                if (user != null) {
                                    buyViewModel.purchaseRoute(currentRouteId, routeName, user)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
