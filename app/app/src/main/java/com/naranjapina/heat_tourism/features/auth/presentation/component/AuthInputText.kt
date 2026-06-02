package com.naranjapina.heat_tourism.features.auth.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R

@Composable
fun AuthInputText(
    isSecret: Boolean = false,
    label: String,
    value: String,
    placeholder: String,
    enabled: Boolean = true,
    changeValue: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    icon: ImageVector
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        TextField(
            visualTransformation =
                if (isSecret) PasswordVisualTransformation()
                else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            value = value,
            placeholder = {
                Text(placeholder)
            },
            onValueChange = changeValue,
            leadingIcon = {
                Icon(
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
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = colorResource(R.color.red_50),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
        )
    }
}