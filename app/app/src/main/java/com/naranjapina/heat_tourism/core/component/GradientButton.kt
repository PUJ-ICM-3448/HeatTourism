package com.naranjapina.heat_tourism.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.naranjapina.heat_tourism.core.utils.redToOrangeGradientBrush

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        content = {
            Text(text, modifier = Modifier.padding(2.dp, 0.dp))
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        ),
        modifier = modifier
            .background(
                brush = redToOrangeGradientBrush(),
                shape = RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(15.dp)
    )
}