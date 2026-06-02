package com.naranjapina.heat_tourism.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.navigation.Screen


@Composable
fun HorizontalDestinationCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    data: DestinationCardData,
    onClick: (() -> Unit)? = null
) {
    val (imgUrl, contentDescription, destinationScore, destinationName) = data

    Row(
        modifier = modifier
            .shadow(3.dp, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .clickable(onClick = {
                onClick?.invoke() ?: navController.navigate(Screen.RouteOverview.name)
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .weight(.3f)
                .fillMaxHeight(),
            model = imgUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .weight(.7f)
                .padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier,
                    text = destinationName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
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

            Text("Información extra")
        }

    }
}