package com.naranjapina.heat_tourism.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.naranjapina.heat_tourism.R

@Composable

fun GroupPublication(
    modifier: Modifier = Modifier,
    imgUrl: String,
    autorName: String,
    location: String,
    age: String,
    contentDescription: String,
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colorResource(R.color.red_50),
                shape = RoundedCornerShape(15.dp)
            )
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(Color.White)
    ) {
        AsyncImage(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            model = imgUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                modifier = Modifier,
                text = autorName,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = Modifier,
                text = location,
                fontSize = 12.sp,
                color = colorResource(
                    R.color.neutral_600
                )
            )
            Text(
                modifier = Modifier,
                text = "Hace $age",
                fontSize = 10.sp,
                color = colorResource(
                    R.color.neutral_600
                )
            )
        }
    }
}