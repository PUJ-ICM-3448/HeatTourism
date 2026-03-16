package com.naranjapina.heat_tourism.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.naranjapina.heat_tourism.R

@Composable
fun GradientText(text: String, fontSize: TextUnit, fontWeight: FontWeight) {
    Text(
        text,
        style = TextStyle(
            brush = Brush.linearGradient(
                colors = listOf(
                    colorResource(R.color.red_400),
                    colorResource(R.color.orange_400),
                )
            )
        ),
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}