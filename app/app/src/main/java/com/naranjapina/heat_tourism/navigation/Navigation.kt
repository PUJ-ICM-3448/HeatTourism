package com.naranjapina.heat_tourism.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.naranjapina.heat_tourism.screen.BuyScreen
import com.naranjapina.heat_tourism.screen.ChatScreen
import com.naranjapina.heat_tourism.screen.ChatsListScreen
import com.naranjapina.heat_tourism.screen.CheckInScreen
import com.naranjapina.heat_tourism.screen.CompanyScreen
import com.naranjapina.heat_tourism.screen.CreatePostScreen
import com.naranjapina.heat_tourism.screen.FriendScreen
import com.naranjapina.heat_tourism.screen.LogInCoordinatorScreen
import com.naranjapina.heat_tourism.screen.LogInScreen
import com.naranjapina.heat_tourism.screen.ManageCompanyScreen
import com.naranjapina.heat_tourism.screen.MapScreen
import com.naranjapina.heat_tourism.screen.NoTravelHomeScreen
import com.naranjapina.heat_tourism.screen.NotificationsScreen
import com.naranjapina.heat_tourism.screen.PostScreen
import com.naranjapina.heat_tourism.screen.ProfileScreen
import com.naranjapina.heat_tourism.screen.RegisterScreen
import com.naranjapina.heat_tourism.screen.RouteGroupScreen
import com.naranjapina.heat_tourism.screen.RouteOverviewScreen
import com.naranjapina.heat_tourism.screen.RouteScreen
import com.naranjapina.heat_tourism.screen.SearcherScreen
import com.naranjapina.heat_tourism.screen.TravelHomeScreen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel
import kotlin.collections.listOf

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
    RouteOverview,
    Friend,
    ChatsList,
    Chat,
    Notifications
}

fun homeRoute(state: String? = null): String =
    if (state == "travel") "${Screen.Home.name}/travel" else Screen.Home.name

@Composable
fun NavigationStack() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel();

    val currentUser by authViewModel.currentUser.collectAsState();

    NavHost (navController, startDestination=
        if(currentUser == null)
            Screen.Register.name
        else homeRoute()
    ) {
        composable(
            route = Screen.Buy.name
        ) {
            BuyScreen(navController)
        }
        composable(
            route = Screen.Chat.name + "/{otherUserId}",
            arguments = listOf(
                navArgument("otherUserId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            val otherUserId = it.arguments?.getString("otherUserId").orEmpty()
            ChatScreen(navController, otherUserId)
        }
        composable(
            route = Screen.ChatsList.name
        ) {
            ChatsListScreen(navController)
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
            route = Screen.Friend.name
        ) {
            FriendScreen(navController)
        }
        composable(
            route = Screen.Home.name
        ) {
            NoTravelHomeScreen(navController)
        }
        composable(
            route = "${Screen.Home.name}/travel"
        ) {
            TravelHomeScreen(navController)
        }
        composable(
            route = Screen.LogIn.name
        ) {
            LogInScreen(authViewModel, navController)
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
            route = Screen.Notifications.name
        ) {
            NotificationsScreen(navController)
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
                navController
            )
        }
        composable(
            route = Screen.Register.name
        ) {
            RegisterScreen(authViewModel, navController)
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
