package com.naranjapina.heat_tourism.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.naranjapina.heat_tourism.ui.theme.Beige
import com.naranjapina.heat_tourism.ui.theme.MediumBeige

@Composable
fun beigeGradientBrush(): Brush {
  return Brush.linearGradient(
    colors = listOf(
      MediumBeige,
      Beige
    ),
  )
}
