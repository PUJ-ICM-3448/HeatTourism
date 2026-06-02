package com.naranjapina.heat_tourism.features.company.presentation.Company.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.naranjapina.heat_tourism.features.company.presentation.Company.screen.manage.ManageCompanyScreen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

@Composable
fun CompanyScreen(navController: NavHostController, authViewModel: AuthViewModel) {

    ManageCompanyScreen(navController, authViewModel)

}