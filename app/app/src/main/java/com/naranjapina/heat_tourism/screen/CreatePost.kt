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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.DestinationCardData
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.component.HorizontalDestinationCard
import com.naranjapina.heat_tourism.component.LocationPicker
import com.naranjapina.heat_tourism.component.TitleAndButton
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.utils.BeigeGradientBrush
import com.naranjapina.heat_tourism.utils.bottomBorder

@Composable
fun CreatePostScreen(navController: NavHostController) {
    Scaffold(
        containerColor = colorResource(R.color.beige)
    ) {paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(
                0.dp, 0.dp, 0.dp, paddingValues.calculateBottomPadding()
            )
        ) {
            item {
                Row(
                    modifier = Modifier
                        .background(
                            Color.White
                        )
                        .fillMaxWidth()
                        .bottomBorder(
                            strokeWidth = 1.dp,
                            color = colorResource(R.color.red_100)
                        )
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
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
                            .clickable(
                                onClick = {
                                    if(navController.previousBackStackEntry == null)
                                        navController.navigate(Screen.Home.name)
                                    else navController.navigateUp()
                                }
                            )
                    )
                    Text(
                        text= "Nueva Publicación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    GradientButton(
                        text = "Publicar"
                    ) {}
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(15.dp, 0.dp)
                ) {
                    Text(
                        text = "Foto del lugar",
                        color = colorResource(R.color.deep_beige),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(15.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(
                                brush = BeigeGradientBrush(),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .border(
                                shape = RoundedCornerShape(15.dp),
                                color = colorResource(R.color.red_50),
                                width = 1.dp
                            ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "Camera",
                            colorFilter = ColorFilter.tint(
                                colorResource(R.color.red_400)
                            ),
                            modifier = Modifier
                                .background(
                                    color = colorResource(R.color.red_400).copy(.25f),
                                    shape = RoundedCornerShape(percent = 100)
                                )
                                .padding(20.dp)
                        )
                        Spacer(
                            Modifier.height(10.dp)
                        )
                        Text(
                            text = "Tomar Foto",
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.deep_beige)
                        )

                        Spacer(
                            Modifier.height(5.dp)
                        )
                        Text(
                            text = "O seleccionar de galeria",
                            color = colorResource(R.color.deep_beige)
                        )
                    }
                }
            }

            item {
                LocationPicker(
                    modifier = Modifier.padding(15.dp, 0.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(15.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Descripción",
                        color = colorResource(R.color.deep_beige),
                        fontSize = 18.sp
                    )
                    TextField(
                        singleLine = false,
                        minLines = 5,
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp))
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(15.dp),
                                color = colorResource(R.color.red_50)
                            ),
                        colors = TextFieldDefaults.
                            colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                        placeholder = {
                            Text("Comparte tu experiencia")
                        }
                    )
                    Text(
                        text = "0/500 caracteres",
                        color = colorResource(R.color.deep_beige).copy(.7f),
                        fontSize = 14.sp
                    )
                }
            }

            item { Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                TitleAndButton(
                    "Ruta",
                    "Cambiar"
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

