package com.naranjapina.heat_tourism.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R

@Composable

fun LocationPicker(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.red_100).copy(.15f),
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                color = colorResource(R.color.red_100),
                width = 1.dp,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Image(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = "Ubicación",
            colorFilter = ColorFilter.tint(
                colorResource(R.color.red_400)
            ),
            modifier = Modifier
                .background(
                    color = colorResource(R.color.red_100).copy(.4f),
                    shape = RoundedCornerShape(
                        25.dp
                    )
                )
                .padding(10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                Text(
                    text = "Ubicación detectada",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Park Güell, Barcelona"
                )
            }
            Text(
                text = "Cambiar",
                color = colorResource(R.color.red_400)
            )
        }
    }
}