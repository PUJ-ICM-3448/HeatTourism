package com.naranjapina.heat_tourism.screen
import android.widget.Space
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.ActionCardGridItem
import com.naranjapina.heat_tourism.component.CardRow
import com.naranjapina.heat_tourism.component.DestinationCard
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.component.GroupPublication
import com.naranjapina.heat_tourism.component.JustInputText
import com.naranjapina.heat_tourism.component.TemperatureWidget
import com.naranjapina.heat_tourism.component.LazyFilterChipRow
import com.naranjapina.heat_tourism.component.TitleAndButton
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.navigation.homeRoute
import com.naranjapina.heat_tourism.utils.RedToOrangeGradientBrush
import com.naranjapina.heat_tourism.utils.mockDestinations
import com.naranjapina.heat_tourism.utils.mockPublications
import com.naranjapina.heat_tourism.utils.rememberAmbientTemperature
import com.naranjapina.heat_tourism.utils.rememberShakeDetector
import kotlinx.coroutines.launch

@Composable
fun NoTravelHomeScreen(navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var destinations by remember {
        mutableStateOf(mockDestinations.shuffled().take(4))
    }

    rememberShakeDetector {
        destinations = mockDestinations.shuffled().take(4)
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
                        .background(brush = RedToOrangeGradientBrush())
                        .fillMaxWidth()
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp ,0.dp)
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
                GradientButton(
                    modifier = Modifier.padding(15.dp).fillMaxWidth(),
                    text = "Ir a menu con Viaje"
                ) {
                    navController.navigate(homeRoute("travel"))
                }
            }
        }
    }
}

@Composable
fun TravelHomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val ambientTemp = rememberAmbientTemperature()
    var publications by remember {
        mutableStateOf(mockPublications.shuffled().take(2))
    }

    rememberShakeDetector {
        publications = mockPublications.shuffled().take(2)
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
                        .background(brush = RedToOrangeGradientBrush())
                        .fillMaxWidth()
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp ,0.dp)
                        .padding(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column() {
                            Text(
                                text = "Viaje activo",
                                color = Color.LightGray,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = "Barcelona Explorer",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "En curso",
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .background(colorResource(R.color.neutral_100).copy(alpha = 0.25f))
                                .padding(10.dp)
                            ,
                            fontSize = 14.sp,
                        )
                    }
                    Text(
                        text = "Barcelona, España",
                        color = Color.White,
                    )

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .background(colorResource(R.color.neutral_100).copy(alpha = 0.25f))
                            .padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Progreso del viaje",
                                color = colorResource(R.color.neutral_200)
                            )
                            Text(
                                text = "2/5 puntos",
                                color = colorResource(R.color.neutral_100)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(colorResource(R.color.neutral_200).copy(.35f))
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(2f)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = Color.White,
                                thickness = 10.dp
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(5f),
                                color = Color.Transparent,
                                thickness = 10.dp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TemperatureWidget(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth(),
                    state = ambientTemp,
                    destinationName = "Barcelona",
                    destinationTempC = 32f
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                CardRow(
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxWidth()
                        .height(100.dp),
                    listOf(
                        ActionCardGridItem(
                            "Mapa",
                            painter = R.drawable.map,
                            color = R.color.red_400,
                            subtitle = null
                        ),
                        ActionCardGridItem(
                            "Check-in",
                            painter = R.drawable.map,
                            color = R.color.orange_400,
                            subtitle = null
                        ),
                        ActionCardGridItem(
                            "Publicar",
                            painter = R.drawable.map,
                            color = R.color.orange_400,
                            subtitle = null
                        ),
                        ActionCardGridItem(
                            "Chat",
                            painter = R.drawable.map,
                            color = R.color.red_400,
                            subtitle = null
                        ),
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TitleAndButton("Próximas paradas", "Ver ruta")
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Text("Aun no hay paradas JASJDKASJDK")
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                TitleAndButton("Publicaciones del grupo", "Ver todas")
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Crossfade(
                    targetState = publications,
                    animationSpec = tween(durationMillis = 500),
                    label = "publications"
                ) { pubs ->
                    Row(
                        Modifier.padding(15.dp, 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        pubs.forEach { pub ->
                            GroupPublication(
                                modifier = Modifier.height(200.dp).weight(1f),
                                imgUrl = pub.imgUrl,
                                location = pub.location,
                                autorName = pub.autorName,
                                contentDescription = pub.contentDescription,
                                age = pub.age
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                GradientButton(
                    modifier = Modifier.padding(15.dp).fillMaxWidth(),
                    text = "Volver a menu sin Viaje"
                ) {
                    navController.navigate(homeRoute())
                }
            }

        }
    }
}
