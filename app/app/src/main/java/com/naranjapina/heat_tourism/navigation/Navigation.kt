package com.naranjapina.heat_tourism.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Screen {
    Home,
    Register,
    LogIn,
    Route,
    Profile,
    Company,
    Buy,
    Searcher,
    CreatePost,
    Post,
    Map,
    ManageCompany,
    LogInCoordinator,
    RouteGroup,
    CheckIn,
    RouteOverview
}

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    NavHost (navController, startDestination= Screen.Home.name) {
        composable(
            route = Screen.Home.name
        ) {
        }
    }
}
