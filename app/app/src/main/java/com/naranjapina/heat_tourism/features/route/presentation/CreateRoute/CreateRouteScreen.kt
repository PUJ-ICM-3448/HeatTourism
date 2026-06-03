package com.naranjapina.heat_tourism.features.route.presentation.CreateRoute

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.utils.bottomBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRouteScreen(
    navController: NavHostController,
    viewModel: CreateRouteViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSavedSuccess, state.errorMessage) {
        if (state.isSavedSuccess) {
            Toast.makeText(context, "Ruta creada con éxito", Toast.LENGTH_SHORT).show()
            viewModel.resetSaveStatus()
            navController.popBackStack()
        }
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
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
                    text = "Gestión de Ruta (Admin)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colorResource(R.color.red_100)
                )
                Spacer(modifier = Modifier.width(35.dp)) // Equalizer spacer
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Route Name Input
                item {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Nombre de la Ruta") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.red_100),
                            focusedLabelColor = colorResource(R.color.red_100)
                        )
                    )
                }

                // Description Input
                item {
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        label = { Text("Descripción del itinerario") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.red_100),
                            focusedLabelColor = colorResource(R.color.red_100)
                        )
                    )
                }

                // Duration and Price Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.duration,
                            onValueChange = { viewModel.onDurationChange(it) },
                            label = { Text("Duración") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.red_100),
                                focusedLabelColor = colorResource(R.color.red_100)
                            )
                        )
                        OutlinedTextField(
                            value = state.price,
                            onValueChange = { viewModel.onPriceChange(it) },
                            label = { Text("Precio") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.red_100),
                                focusedLabelColor = colorResource(R.color.red_100)
                            )
                        )
                    }
                }

                // Stops Section Title
                item {
                    Text(
                        text = "Planificación de Paradas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorResource(R.color.red_100),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Add Stop Input and Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = state.currentStopInput,
                            onValueChange = { viewModel.onCurrentStopInputChange(it) },
                            label = { Text("Nueva Parada") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.red_100),
                                focusedLabelColor = colorResource(R.color.red_100)
                            )
                        )
                        Button(
                            onClick = { viewModel.addStop() },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.red_100)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Añadir", color = Color.White)
                        }
                    }
                }

                // Added Stops List
                if (state.stops.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin paradas. Agrega al menos una parada.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(state.stops) { stop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                                .padding(12.dp, 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stop,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.removeStop(stop) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Eliminar parada",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }

                // Save Route Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    GradientButton(
                        text = if (state.isLoading) "Guardando..." else "Crear Ruta",
                        color = colorResource(R.color.red_100),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.saveRoute() }
                    )
                }
            }
        }
    }
}
