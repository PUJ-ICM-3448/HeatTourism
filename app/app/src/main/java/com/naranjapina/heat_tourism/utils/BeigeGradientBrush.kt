package com.naranjapina.heat_tourism.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import com.naranjapina.heat_tourism.R

@Composable
fun BeigeGradientBrush (): Brush {
  return Brush.linearGradient(
        colors = listOf(
            colorResource(R.color.medium_beige),
            colorResource(R.color.beige),
        ),
    )
}
