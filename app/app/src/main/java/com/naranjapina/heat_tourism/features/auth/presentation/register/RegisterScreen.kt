package com.naranjapina.heat_tourism.features.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.GradientButton
import com.naranjapina.heat_tourism.core.component.GradientText
import com.naranjapina.heat_tourism.core.component.TextDivider
import com.naranjapina.heat_tourism.core.component.brand.HeatTourismFullLogo
import com.naranjapina.heat_tourism.features.auth.presentation.component.AuthInputText
import com.naranjapina.heat_tourism.features.auth.presentation.component.GoogleButton
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    authViewModel: AuthViewModel,
    onGoToLogin: () -> Unit,
    onGoToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val currentUser by authViewModel.currentUser.collectAsState()
    LaunchedEffect(
        currentUser
    ) {
        if (currentUser != null) onGoToHome()
    }

    Scaffold(
        containerColor = colorResource(R.color.beige)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(36.dp, 48.dp)
                .fillMaxSize(),
        ) {
            HeatTourismFullLogo()

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    Column {
                        Text(
                            text = "Crear cuenta",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        )

                        Text(
                            text = "Únete y explora el mundo en tiempo real",
                            fontSize = 16.sp
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        AuthInputText(
                            label = "Nombre completo",
                            value = state.fullName.orEmpty(),
                            changeValue = { it ->
                                viewModel.onFullNameChange(it)
                            },
                            placeholder = "Tu nombre",
                            icon = Icons.Outlined.Person,
                            enabled = !authViewModel.isLoading
                        )
                        AuthInputText(
                            label = "Correo Electronico",
                            value = state.email.orEmpty(),
                            changeValue = { it ->
                                viewModel.onEmailChange(it)
                            },
                            placeholder = "tu@email.com",
                            icon = Icons.Outlined.Email,
                            enabled = !authViewModel.isLoading
                        )

                        AuthInputText(
                            label = "Contraseña",
                            value = state.password.orEmpty(),
                            changeValue = { it ->
                                viewModel.onPasswordChange(it)
                            },
                            placeholder = "Mínimo 8 caracteres",
                            icon = Icons.Outlined.Lock,
                            isSecret = true,
                            enabled = !authViewModel.isLoading
                        )

                        if (authViewModel.feedbackMessage != null) {
                            Text(
                                text = authViewModel.feedbackMessage!!,
                                color = colorResource(R.color.red_400),
                                textAlign = TextAlign.Center
                            )
                        }

                        GradientButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Crear cuenta",
                            enabled = !state.isLoading
                        ) {
                            // TODO: LUEGO AGREGAR CON LA DB BIEN EL NOMBRE
                            viewModel.onRegisterEvent()
                        }

                        TextDivider("o registrate con")

                        GoogleButton(
                            enabled = !state.isLoading
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "¿Ya tienes cuenta?",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            GradientText(
                                text = "Inicia sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                onClick = onGoToLogin
                            )
                        }
                    }

                }
            }

        }
    }
}

