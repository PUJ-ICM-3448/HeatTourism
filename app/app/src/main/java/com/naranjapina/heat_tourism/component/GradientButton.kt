package com.naranjapina.heat_tourism.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.naranjapina.heat_tourism.R

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        content = {
            Text(text, modifier = Modifier.padding(5.dp))
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().background(
            brush = Brush.linearGradient(
                colors = listOf(
                    colorResource(R.color.red_400),
                    colorResource(R.color.orange_400),
                ),
            ),
            shape = RoundedCornerShape(15.dp)
        ).clip(RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(15.dp)
    );
}