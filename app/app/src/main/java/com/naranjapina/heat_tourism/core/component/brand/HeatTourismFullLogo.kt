package com.naranjapina.heat_tourism.core.component.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.GradientText

@Composable
fun HeatTourismFullLogo() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = "Icono",
            modifier = Modifier
                .width(55.dp)
                .height(55.dp)
        )
        Spacer(
            modifier = Modifier.width(15.dp)
        )
        GradientText(
            text = "HeatTourism",
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}