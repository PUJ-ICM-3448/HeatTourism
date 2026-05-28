package com.naranjapina.heat_tourism.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.naranjapina.heat_tourism.core.design.theme.Beige
import com.naranjapina.heat_tourism.core.design.theme.MediumBeige

@Composable
fun beigeGradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            MediumBeige,
            Beige
        ),
    )
}
