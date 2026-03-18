package com.naranjapina.heat_tourism.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.navigation.Screen


data class DestinationCardData (
    val imgUrl: String,
    val contentDescription: String,
    val destinationScore: Float,
    val destinationName: String
)

@Composable
fun DestinationCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    data: DestinationCardData
) {
    val (imgUrl, contentDescription, destinationScore, destinationName) = data

    Column(
        modifier = modifier.shadow(3.dp, shape = RoundedCornerShape(15.dp)).clip(
            RoundedCornerShape(15.dp)
        ).background(Color.White)
    ) {
        AsyncImage(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            model = imgUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds
        )
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = destinationName,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.star),
                        contentDescription = "Estrella",
                        colorFilter = ColorFilter.tint(
                            colorResource(R.color.orange_300)
                        )
                    )
                    Text(
                        text = "%.1f".format(destinationScore),
                        color = Color.Gray
                    )
                }
            }
            Spacer(
                modifier = Modifier.width(15.dp)
            )
            GradientButton(
                modifier = Modifier,
                text = "Explorar"
            ) {
                navController.navigate(Screen.Route.name)
            }
        }
    }
}