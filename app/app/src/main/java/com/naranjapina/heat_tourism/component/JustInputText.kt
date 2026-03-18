package com.naranjapina.heat_tourism.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun JustInputText(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    changeValue: (String) -> Unit,
    icon: ImageVector) {
    TextField(
        value= value,
        placeholder = {
            Text(placeholder)
        },
        onValueChange = changeValue,
        leadingIcon = {
            Icon(
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
                painter = rememberVectorPainter(
                    icon
                ),
                contentDescription = ""
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    )
}