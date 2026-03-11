package com.naranjapina.heat_tourism.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.naranjapina.heat_tourism.screen.BuyScreen
import com.naranjapina.heat_tourism.screen.CheckInScreen
import com.naranjapina.heat_tourism.screen.CompanyScreen
import com.naranjapina.heat_tourism.screen.CreatePostScreen
import com.naranjapina.heat_tourism.screen.HomeScreen
import com.naranjapina.heat_tourism.screen.LogInCoordinatorScreen
import com.naranjapina.heat_tourism.screen.LogInScreen
import com.naranjapina.heat_tourism.screen.ManageCompanyScreen
import com.naranjapina.heat_tourism.screen.MapScreen
import com.naranjapina.heat_tourism.screen.PostScreen
import com.naranjapina.heat_tourism.screen.ProfileScreen
import com.naranjapina.heat_tourism.screen.RegisterScreen
import com.naranjapina.heat_tourism.screen.RouteGroupScreen
import com.naranjapina.heat_tourism.screen.RouteOverviewScreen
import com.naranjapina.heat_tourism.screen.RouteScreen
import com.naranjapina.heat_tourism.screen.SearcherScreen

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

    NavHost (navController, startDestination= Screen.LogIn.name) {
        composable(
            route = Screen.Buy.name
        ) {
            BuyScreen(navController)
        }
        composable(
            route = Screen.CheckIn.name
        ) {
            CheckInScreen(navController)
        }
        composable(
            route = Screen.Company.name
        ) {
            CompanyScreen(navController)
        }
        composable(
            route = Screen.CreatePost.name
        ) {
            CreatePostScreen(navController)
        }
        composable(
            route = Screen.Home.name
        ) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.LogIn.name
        ) {
            LogInScreen(navController)
        }
        composable(
            route = Screen.LogInCoordinator.name
        ) {
            LogInCoordinatorScreen(navController)
        }
        composable(
            route = Screen.ManageCompany.name
        ) {
            ManageCompanyScreen(navController)
        }
        composable(
            route = Screen.Map.name
        ) {
            MapScreen(navController)
        }
        composable(
            route = Screen.Post.name
        ) {
            PostScreen(navController)
        }
        composable(
            route = Screen.Profile.name
        ) {
            ProfileScreen(navController)
        }
        composable(
            route = Screen.Register.name
        ) {
            RegisterScreen(navController)
        }
        composable(
            route = Screen.Route.name
        ) {
            RouteScreen(navController)
        }
        composable(
            route = Screen.RouteGroup.name
        ) {
            RouteGroupScreen(navController)
        }
        composable(
            route = Screen.RouteOverview.name
        ) {
            RouteOverviewScreen(navController)
        }
        composable(
            route = Screen.Searcher.name
        ) {
            SearcherScreen(navController)
        }
    }
}
