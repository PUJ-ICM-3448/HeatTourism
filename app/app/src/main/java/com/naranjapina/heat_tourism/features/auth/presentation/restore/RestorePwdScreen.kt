package com.naranjapina.heat_tourism.features.auth.presentation.restore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.features.auth.presentation.component.AuthInputText

/**
 * Pantalla para recuperar contrasena via Firebase Auth.
 * Envia un correo de reset al email ingresado.
 */
@Composable
fun RestorePwdScreen(
    onGoBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<RestoreStatus>(RestoreStatus.Idle) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.beige))
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Volver",
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(100)
                    )
                    .clip(RoundedCornerShape(100))
                    .clickable { onGoBack() }
                    .padding(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recuperar contrasena",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.red_400)
            )

            Text(
                text = "Ingresa el correo asociado a tu cuenta. Te enviaremos un enlace para restablecer tu contrasena.",
                color = colorResource(R.color.neutral_500),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            AuthInputText(
                label = "Correo electronico",
                value = email,
                placeholder = "tucorreo@ejemplo.com",
                icon = Icons.Outlined.Email,
                changeValue = { email = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            when (val current = status) {
                is RestoreStatus.Error -> Text(
                    text = current.message,
                    color = colorResource(R.color.red_500),
                    fontSize = 13.sp
                )
                RestoreStatus.Success -> Text(
                    text = "Correo enviado. Revisa tu bandeja de entrada.",
                    color = colorResource(R.color.orange_500),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                else -> {}
            }

            Spacer(modifier = Modifier.height(10.dp))

            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (status == RestoreStatus.Sending) "Enviando..." else "Enviar enlace"
            ) {
                if (email.isBlank()) {
                    status = RestoreStatus.Error("Ingresa un correo valido")
                    return@GradientButton
                }
                status = RestoreStatus.Sending
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener { status = RestoreStatus.Success }
                    .addOnFailureListener { ex ->
                        status = RestoreStatus.Error(
                            ex.localizedMessage ?: "No se pudo enviar el correo"
                        )
                    }
            }
        }
    }
}

private sealed interface RestoreStatus {
    data object Idle : RestoreStatus
    data object Sending : RestoreStatus
    data object Success : RestoreStatus
    data class Error(val message: String) : RestoreStatus
}
