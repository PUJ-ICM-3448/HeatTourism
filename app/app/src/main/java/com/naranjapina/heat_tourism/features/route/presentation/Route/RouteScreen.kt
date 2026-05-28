package com.naranjapina.heat_tourism.features.route.presentation.Route

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.ActionCardGridItem
import com.naranjapina.heat_tourism.core.component.CardRow
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.component.TitleAndButton
import com.naranjapina.heat_tourism.core.navigation.Screen

@Composable
fun RouteScreen(navController: NavHostController) {
    Scaffold { paddingValues ->
        LazyColumn {
            item {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = "https://aerobusbarcelona.es/wp-content/uploads/2024/05/AdobeStock_296329621_Editorial_Use_Only-scaled.jpeg",
                        contentDescription = "Barcelona Clásica",
                        contentScale = ContentScale.FillBounds
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp, 0.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier
                                    .background(
                                        color = Color.White.copy(.8f),
                                        shape = RoundedCornerShape(100)
                                    )
                                    .padding(10.dp)
                                    .clickable(
                                        onClick = { navController.navigate(Screen.Home.name) }
                                    )
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .background(
                                            color = Color.White.copy(.8f),
                                            shape = RoundedCornerShape(100)
                                        )
                                        .padding(10.dp)
                                )
                                Icon(
                                    imageVector = Icons.Outlined.AccountBox,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .background(
                                            color = Color.White.copy(.8f),
                                            shape = RoundedCornerShape(100)
                                        )
                                        .padding(10.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(.5f),
                                            Color.Black.copy(.7f)
                                        )
                                    )
                                )
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Text(
                                text = "Barcelona Clásica",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                            Text(
                                text = "Estrellas",
                                color = Color.White
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier.padding(15.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = "Sobre esta ruta",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp
                    )
                    Text("Descubre los lugares más emblemáticos de Barcelona en esta ruta diseñada para experimentar lo mejor de la ciudad. Desde la arquitectura de Gaudi hasta la vibrante vida de las Ramblas.")
                }
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                CardRow(
                    modifier = Modifier
                        .height(150.dp)
                        .padding(15.dp, 0.dp),
                    list = listOf(
                        ActionCardGridItem(
                            title = "4-5 horas",
                            subtitle = "Duración",
                            painter = R.drawable.clock,
                            color = R.color.red_400
                        ),
                        ActionCardGridItem(
                            title = "8 paradas",
                            subtitle = "Lugares",
                            painter = R.drawable.map,
                            color = R.color.orange_400
                        ),
                        ActionCardGridItem(
                            title = "Fácil",
                            subtitle = "Nivel",
                            painter = R.drawable.group,
                            color = R.color.orange_400
                        ),
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TitleAndButton("Empresas Asociadas", "Ver todas")
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TitleAndButton("Experiencias recientes", "Ver todas")
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier.padding(15.dp, 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    GradientButton(
                        modifier = Modifier.weight(1f),
                        text = "Unirse a la ruta"
                    ) { }
                    GradientButton(
                        modifier = Modifier.weight(1f),
                        text = "Ver en mapa"
                    ) {
                        // Bloque B: por defecto enseno la ruta hasta Monserrate.
                        // Cuando exista detalle de ruta real, pasar el id correspondiente.
                        navController.navigate(
                            "${Screen.RouteOverview.name}?destinationId=monserrate"
                        )
                    }
                }
            }
        }
    }
}

