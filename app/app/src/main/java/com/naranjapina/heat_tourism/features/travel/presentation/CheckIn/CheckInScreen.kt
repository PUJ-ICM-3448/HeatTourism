package com.naranjapina.heat_tourism.features.travel.presentation.CheckIn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    navController: NavHostController,
    groupId: String,
    viewModel: CheckInViewModel = viewModel()
) {
    val members by viewModel.members.collectAsState()

    // Filtrar solo los que están en PENDING para la variante de aprobación
    val pendingMembers = members.filter { it.checkInStatus == "PENDING" }

    LaunchedEffect(groupId) {
        viewModel.loadMembers(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aprobación de Check-In", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32)) // Green for Coordinator
            )
        }
    ) { padding ->
        if (pendingMembers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay check-ins pendientes", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendingMembers) { member ->
                    PassengerCard(
                        member = member,
                        onApprove = { viewModel.updateStatus(groupId, member.id!!, "APPROVED") },
                        onReject = { viewModel.updateStatus(groupId, member.id!!, "REJECTED") },
                        onTogglePresence = { isPresent -> viewModel.markAttendance(groupId, member.id!!, isPresent) }
                    )
                }
            }
        }
    }
}

@Composable
fun PassengerCard(
    member: com.naranjapina.heat_tourism.features.travel.data.model.MiembroGrupo,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onTogglePresence: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = member.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = "Check-In: ${member.checkInStatus}",
                        color = Color.Gray
                    )
                }
                Row {
                    IconButton(onClick = onApprove) {
                        Icon(Icons.Default.Check, contentDescription = "Aprobar", tint = Color(0xFF2E7D32))
                    }
                    IconButton(onClick = onReject) {
                        Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = Color.Red)
                    }
                }
            }
        }
    }
}
