package com.naranjapina.heat_tourism.features.auth.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.component.brand.HeatTourismFullLogo
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

/**
 * Pantalla de splash. Muestra el logo mientras AuthViewModel verifica
 * si hay sesion activa y luego navega a Home o LogIn.
 */
@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onGoToHome: () -> Unit,
    onGoToLogIn: () -> Unit
) {
    val state by authViewModel.state.collectAsState()

    LaunchedEffect(state.isLoading, state.user) {
        if (!state.isLoading) {
            if (state.user != null) {
                onGoToHome()
            } else {
                onGoToLogIn()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.beige)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeatTourismFullLogo()
        Spacer(modifier = Modifier.height(40.dp))
        CircularProgressIndicator(
            color = colorResource(R.color.red_400)
        )
    }
}
