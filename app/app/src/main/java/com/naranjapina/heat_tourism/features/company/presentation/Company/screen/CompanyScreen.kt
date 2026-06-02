package com.naranjapina.heat_tourism.features.company.presentation.Company.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.naranjapina.heat_tourism.core.navigation.Screen
import com.naranjapina.heat_tourism.data.auth.model.UserRole
import com.naranjapina.heat_tourism.features.company.presentation.Company.screen.manage.ManageCompanyScreen
import com.naranjapina.heat_tourism.features.company.presentation.Company.screen.view.ViewCompanyScreen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

@Composable
fun CompanyScreen(navController: NavHostController, authViewModel: AuthViewModel, companyId: String?) {
    val authState by authViewModel.state.collectAsState();


    if(companyId != null) {
        ViewCompanyScreen(navController, companyId)
    }else if(authState.user != null && authState.user!!.roles.contains(UserRole.ADMINISTRATOR)) {
        ManageCompanyScreen(navController, authViewModel)
    } else {
        navController.navigate(Screen.Home.name)
    }
}