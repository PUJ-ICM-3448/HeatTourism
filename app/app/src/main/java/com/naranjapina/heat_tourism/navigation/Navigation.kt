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

    NavHost (navController, startDestination= Screen.Buy.name) {
        composable(
            route = Screen.Buy.name
        ) {
            BuyScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.CheckIn.name) {
        composable(
            route = Screen.CheckIn.name
        ) {
            CheckInScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Company.name) {
        composable(
            route = Screen.Company.name
        ) {
            CompanyScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.CreatePost.name) {
        composable(
            route = Screen.CreatePost.name
        ) {
            CreatePostScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Home.name) {
        composable(
            route = Screen.Home.name
        ) {
            HomeScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.LogIn.name) {
        composable(
            route = Screen.LogIn.name
        ) {
            LogInScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.LogInCoordinator.name) {
        composable(
            route = Screen.LogInCoordinator.name
        ) {
            LogInCoordinatorScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.ManageCompany.name) {
        composable(
            route = Screen.ManageCompany.name
        ) {
            ManageCompanyScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Map.name) {
        composable(
            route = Screen.Map.name
        ) {
            MapScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Post.name) {
        composable(
            route = Screen.Post.name
        ) {
            PostScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Profile.name) {
        composable(
            route = Screen.Profile.name
        ) {
            ProfileScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Register.name) {
        composable(
            route = Screen.Register.name
        ) {
            RegisterScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Route.name) {
        composable(
            route = Screen.Route.name
        ) {
            RouteScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.RouteGroup.name) {
        composable(
            route = Screen.RouteGroup.name
        ) {
            RouteGroupScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.RouteOverview.name) {
        composable(
            route = Screen.RouteOverview.name
        ) {
            RouteOverviewScreen(navController)
        }
    }

    NavHost (navController, startDestination= Screen.Searcher.name) {
        composable(
            route = Screen.Searcher.name
        ) {
            SearcherScreen(navController)
        }
    }
}
