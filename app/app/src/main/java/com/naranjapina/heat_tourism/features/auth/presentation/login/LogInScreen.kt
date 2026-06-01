package com.naranjapina.heat_tourism.features.auth.presentation.login

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
fun LogInScreen(authViewModel: AuthViewModel,
                viewModel: LoginViewModel,
                onGoToHome:() -> Unit,
                onGoToRegister:() -> Unit,
) {
    val state: LoginState by viewModel.state.collectAsState()

    LaunchedEffect(
        state.user
    ) {
        if (state.user != null) {
            onGoToHome()
            authViewModel.onUpdateUser(state.user!!);
        }

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
                            text = "Bienvenido",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        )

                        Text(
                            text = "Inicia sesión para continuar explorando",
                            fontSize = 16.sp
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        AuthInputText(
                            label = "Correo Electronico",
                            value = state.email,
                            changeValue = {it ->
                                viewModel.onEmailChange(it)
                            },
                            placeholder = "tu@email.com",
                            icon = Icons.Outlined.Email,
                            enabled = !state.isLoading
                        )

                        AuthInputText(
                            label = "Contraseña",
                            value = state.password,
                            changeValue = {it ->
                                viewModel.onPasswordChange(it)
                            },
                            placeholder = "Mínimo 8 caracteres",
                            icon = Icons.Outlined.Lock,
                            enabled = !state.isLoading,
                            isSecret = true
                        )

                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = colorResource(R.color.red_400),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        )

                        if (state.error != null) {
                            Text(
                                text = state.error!!,
                                color = colorResource(R.color.red_400),
                                textAlign = TextAlign.Center
                            )
                        }

                        GradientButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Iniciar sesión",
                            enabled = !state.isLoading
                        ) {
                            viewModel.onLoginEvent()
                        }

                        TextDivider("o continúa con")

                        GoogleButton(
                            enabled = !state.isLoading
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "¿No tienes cuenta?",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            GradientText(
                                text = "Regístrate",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                onClick = onGoToRegister
                            )
                        }
                    }

                }
            }

        }
    }
}

