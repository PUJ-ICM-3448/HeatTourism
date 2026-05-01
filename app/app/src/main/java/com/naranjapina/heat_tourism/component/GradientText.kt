package com.naranjapina.heat_tourism.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

@Composable
fun GradientText(
  text: String,
  fontSize: TextUnit,
  fontWeight: FontWeight,
  onClick: () -> Unit = {}
) {
  Text(
    text,
    style = TextStyle(
      brush = Brush.linearGradient(
        colors = listOf(
          MaterialTheme.colorScheme.primary,
          MaterialTheme.colorScheme.secondary,
        )
      )
    ),
    modifier = Modifier.clickable(
      onClick = { onClick() }
    ),
    fontSize = fontSize,
    fontWeight = fontWeight
  )
}