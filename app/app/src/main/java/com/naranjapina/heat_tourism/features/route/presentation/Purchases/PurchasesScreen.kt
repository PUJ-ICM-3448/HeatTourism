package com.naranjapina.heat_tourism.features.route.presentation.Purchases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.utils.bottomBorder
import com.naranjapina.heat_tourism.features.travel.data.model.GrupoViaje
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val viewModel: PurchasesViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ruta Actual", "Próximas", "Pasadas")

    LaunchedEffect(authState.user?.id) {
        authState.user?.id?.let { viewModel.loadUserPurchases(it) }
    }

    Scaffold(
        containerColor = colorResource(R.color.beige)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .bottomBorder(
                        strokeWidth = 1.dp,
                        color = colorResource(R.color.red_100)
                    )
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    modifier = Modifier
                        .height(35.dp)
                        .width(35.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Text(
                    text = "Mis Compras",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(35.dp))
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Red,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.Red
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) Color.Red else Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // Routes List
            val currentList = when (selectedTab) {
                0 -> state.currentRoutes
                1 -> state.futureRoutes
                else -> state.pastRoutes
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Red)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tienes rutas en esta sección",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    } else {
                        items(currentList) { route ->
                            PurchasedRouteCard(route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PurchasedRouteCard(route: GrupoViaje) {
    val statusColor = when (route.status) {
        "ACTIVE" -> Color(0xFF4CAF50)
        "PENDING" -> Color(0xFF2196F3)
        else -> Color.Gray
    }

    val statusText = when (route.status) {
        "ACTIVE" -> "En progreso"
        "PENDING" -> "Programado"
        "COMPLETED" -> "Completado"
        "CANCELLED" -> "Cancelado"
        else -> route.status
    }

    val dateStr = remember(route.createdAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(route.createdAt))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = route.routeName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = statusText,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            HorizontalDivider(color = colorResource(R.color.beige), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Fecha / Hora", color = Color.Gray, fontSize = 12.sp)
                    Text(text = dateStr, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Precio", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "Pagado", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Red)
                }
            }
        }
    }
}
