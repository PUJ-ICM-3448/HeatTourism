package com.naranjapina.heat_tourism.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.Image
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.DestinationCardData
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.component.HorizontalDestinationCard
import com.naranjapina.heat_tourism.component.StatRowItemData
import com.naranjapina.heat_tourism.component.StatsRow
import com.naranjapina.heat_tourism.component.TitleAndButton
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, navController: NavHostController) {

    val currentUser by authViewModel.currentUser.collectAsState();
    LaunchedEffect(
        currentUser
    ) {
        if(currentUser == null)
            navController.navigate(Screen.LogIn.name)
    }

    MenuBottonLayout(activeName = "profile", navController = navController) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(
                0.dp, 0.dp, 0.dp, paddingValues.calculateBottomPadding()
            )
        ) {
            item {
                Column(
                    modifier = Modifier
                        .background(colorResource(R.color.red_50))
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text="Mi Perfil",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        )
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null,
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(.8f),
                                    shape = RoundedCornerShape(100)
                                ).clickable(
                                    onClick = {
                                        authViewModel.logOutUser();
                                    }
                                )
                                .padding(10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 3.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(percent=100)
                                    )
                                    .clip(RoundedCornerShape(percent=100)),
                                model = "https://avatars.githubusercontent.com/u/62490806?v=4",
                                contentDescription = "Avatar perfil"
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(110.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Miguel Vargas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Text(
                                text = "En realidad no salgo de casa",
                                color = colorResource(R.color.neutral_600)
                            )
                            GradientButton(
                                text = "Editar perfil"
                            ) { }
                        }
                    }

                    StatsRow(
                        items = listOf(
                            StatRowItemData(
                                title="Viajes",
                                value=12
                            ),
                            StatRowItemData(
                                title="Lugares",
                                value=47
                            ),
                            StatRowItemData(
                                title="Seguidores",
                                value=234
                            ),
                        )
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(15.dp, 0.dp)
                ) {
                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )
                    TitleAndButton("Mis viajes", "Ver todos")
                    HorizontalDestinationCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 0.dp, vertical = 8.dp),
                        data = DestinationCardData(
                            destinationName = "Park Güell, Barcelona",
                            destinationScore = 4.783f,
                            imgUrl = "https://www.outlooktravelmag.com/media/bali-1-1679062958.profileImage.2x-1536x884.webp",
                            contentDescription = ""
                        ),
                        navController = navController
                    )
                    HorizontalDestinationCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 0.dp, vertical = 8.dp),
                        data = DestinationCardData(
                            destinationName = "Park Güell, Barcelona",
                            destinationScore = 4.783f,
                            imgUrl = "https://www.outlooktravelmag.com/media/bali-1-1679062958.profileImage.2x-1536x884.webp",
                            contentDescription = ""
                        ),
                        navController = navController
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(15.dp, 0.dp)
                ) {
                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )
                    TitleAndButton("Lugares guardados", "Ver todos")
                    HorizontalDestinationCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 0.dp, vertical = 8.dp),
                        data = DestinationCardData(
                            destinationName = "Park Güell, Barcelona",
                            destinationScore = 4.783f,
                            imgUrl = "https://www.outlooktravelmag.com/media/bali-1-1679062958.profileImage.2x-1536x884.webp",
                            contentDescription = ""
                        ),
                        navController = navController
                    )
                    HorizontalDestinationCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 0.dp, vertical = 8.dp),
                        data = DestinationCardData(
                            destinationName = "Park Güell, Barcelona",
                            destinationScore = 4.783f,
                            imgUrl = "https://www.outlooktravelmag.com/media/bali-1-1679062958.profileImage.2x-1536x884.webp",
                            contentDescription = ""
                        ),
                        navController = navController
                    )
                }
            }
        }
    }
}

