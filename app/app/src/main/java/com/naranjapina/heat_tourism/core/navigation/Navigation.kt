package com.naranjapina.heat_tourism.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.naranjapina.heat_tourism.features.auth.presentation.login.LogInScreen
import com.naranjapina.heat_tourism.features.auth.presentation.register.RegisterScreen
import com.naranjapina.heat_tourism.features.company.presentation.ManageCompany.CompanyScreen
import com.naranjapina.heat_tourism.features.home.presentation.Home.NoTravelHomeScreen
import com.naranjapina.heat_tourism.features.home.presentation.Home.TravelHomeScreen
import com.naranjapina.heat_tourism.features.map.presentation.Map.MapScreen
import com.naranjapina.heat_tourism.features.map.presentation.Map.RouteOverviewScreen
import com.naranjapina.heat_tourism.features.route.presentation.Buy.BuyScreen
import com.naranjapina.heat_tourism.features.route.presentation.Route.RouteScreen
import com.naranjapina.heat_tourism.features.social.presentation.CreatePost.CreatePostScreen
import com.naranjapina.heat_tourism.features.social.presentation.Post.PostScreen
import com.naranjapina.heat_tourism.features.social.presentation.Profile.ProfileScreen
import com.naranjapina.heat_tourism.features.social.presentation.Searcher.SearcherScreen
import com.naranjapina.heat_tourism.features.travel.presentation.CheckIn.CheckInScreen
import com.naranjapina.heat_tourism.features.travel.presentation.RouteGroup.RouteGroupScreen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

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
    RouteGroup,
    CheckIn,
    RouteOverview
}

@Composable
fun NavigationStack() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    val authState by authViewModel.state.collectAsState();

    NavHost(
        navController, startDestination =
            if (authState.user == null)
                Screen.LogIn.name
            else Screen.Home.name
    ) {
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
            route = "${Screen.Home.name}?state={state}",
            arguments = listOf(
                navArgument("state") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val state = it.arguments?.getString("state")

            if (state == "travel")
                TravelHomeScreen(navController)
            else
                NoTravelHomeScreen(navController)
        }
        composable(
            route = Screen.LogIn.name
        ) {
            LogInScreen(
                authViewModel = authViewModel,
                viewModel = viewModel(),
                onGoToHome = {
                    navController.navigate(Screen.Home.name)
                },
                onGoToRegister = {
                    navController.navigate(Screen.Register.name)
                }
            )
        }
        composable(
            route = Screen.Company.name
        ) {
            CompanyScreen(navController)
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
            ProfileScreen(
                authViewModel,
                navController,
                onGoToCompany = {
                    navController.navigate(Screen.Company.name)
                }
            )
        }
        composable(
            route = Screen.Register.name
        ) {
            RegisterScreen(authViewModel = authViewModel, viewModel = viewModel(), onGoToLogin = {
                navController.navigate(Screen.LogIn.name)
            }, onGoToHome = {
                navController.navigate(Screen.Home.name)
            })
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
            route = "${Screen.RouteOverview.name}?destinationId={destinationId}",
            arguments = listOf(
                navArgument("destinationId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val destinationId = it.arguments?.getString("destinationId")
            RouteOverviewScreen(navController, destinationId)
        }
        composable(
            route = Screen.Searcher.name
        ) {
            SearcherScreen(navController)
        }
    }
}
