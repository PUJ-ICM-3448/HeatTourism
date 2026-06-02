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
import com.naranjapina.heat_tourism.features.company.presentation.Company.screen.CompanyScreen
import com.naranjapina.heat_tourism.features.home.presentation.Home.HomeDispatcher
import com.naranjapina.heat_tourism.features.map.presentation.Map.MapScreen
import com.naranjapina.heat_tourism.features.map.presentation.Map.RouteOverviewScreen
import com.naranjapina.heat_tourism.features.route.presentation.Buy.BuyScreen
import com.naranjapina.heat_tourism.features.route.presentation.CreateRoute.CreateRouteScreen
import com.naranjapina.heat_tourism.features.route.presentation.Purchases.PurchasesScreen
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
    RouteOverview,
    CreateRoute,
    Purchases
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
            route = "${Screen.Buy.name}?routeId={routeId}",
            arguments = listOf(
                navArgument("routeId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val routeId = it.arguments?.getString("routeId")
            BuyScreen(navController, routeId, authViewModel)
        }
        composable(
            route = "${Screen.CheckIn.name}/{groupId}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            CheckInScreen(navController, groupId)
        }
        composable(
            route = Screen.CreatePost.name
        ) {
            CreatePostScreen(navController)
        }
        composable(
            route = Screen.Home.name
        ) {
            HomeDispatcher(navController)
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
            route = Screen.Company.name,
            arguments = listOf(
                navArgument("companyId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val companyId = it.arguments?.getString("companyId")
            CompanyScreen(navController, authViewModel = authViewModel, companyId)
        }
        composable(
            route = Screen.Map.name
        ) {
            MapScreen(navController)
        }
        composable(
            route = "${Screen.Post.name}?postId={postId}",
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val postId = it.arguments?.getString("postId") ?: "default_post_123"
            PostScreen(navController, authViewModel, postId)
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
            RouteOverviewScreen(navController, destinationId, authViewModel)
        }
        composable(
            route = Screen.Searcher.name
        ) {
            SearcherScreen(navController)
        }
        composable(
            route = "create_route"
        ) {
            CreateRouteScreen(navController)
        }
        composable(
            route = Screen.Purchases.name
        ) {
            PurchasesScreen(navController, authViewModel)
        }
    }
}
