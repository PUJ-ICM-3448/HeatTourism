package com.naranjapina.heat_tourism.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

@Composable
fun RedToOrangeGradientBrush(): Brush {
  return Brush.linearGradient(
    colors = listOf(
      MaterialTheme.colorScheme.primary,
      MaterialTheme.colorScheme.secondary
    ),
  )
}