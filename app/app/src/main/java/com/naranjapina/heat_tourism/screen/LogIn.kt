package com.naranjapina.heat_tourism.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.GoogleButton
import com.naranjapina.heat_tourism.component.GradientButton
import com.naranjapina.heat_tourism.component.GradientText
import com.naranjapina.heat_tourism.component.InputText
import com.naranjapina.heat_tourism.component.TextDivider
import com.naranjapina.heat_tourism.navigation.Screen

@Composable
fun LogInScreen(navController: NavHostController) {
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    Scaffold(
        containerColor = colorResource(R.color.beige)
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(36.dp, 48.dp)
                .fillMaxSize(),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = "Icono",
                    modifier = Modifier.width(55.dp).height(55.dp)
                )
                Spacer(
                    modifier = Modifier.width(15.dp)
                )
                GradientText(
                    text="HeatTourism",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    Column() {
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
                        InputText(
                            label = "Correo Electronico",
                            value = email,
                            changeValue = {
                                email = it
                            },
                            placeholder = "tu@email.com",
                            icon = Icons.Outlined.Email
                        )

                        InputText(
                            label = "Contraseña",
                            value = password,
                            changeValue = {
                                password = it
                            },
                            placeholder = "Mínimo 8 caracteres",
                            icon = Icons.Outlined.Lock
                        )

                        Text(
                            text="¿Olvidaste tu contraseña?",
                            color= colorResource(R.color.red_400),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        )

                        GradientButton(
                            modifier = Modifier.fillMaxWidth(),
                            text="Iniciar sesión"
                        ) { navController.navigate(Screen.Home.name) }

                        TextDivider("o continúa con")

                        GoogleButton()

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
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
                                onClick = {
                                    navController.navigate(Screen.Register.name)
                                }
                            )
                        }
                    }

                }
            }

        }
    }
}

