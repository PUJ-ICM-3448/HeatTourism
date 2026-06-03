package com.naranjapina.heat_tourism.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.naranjapina.heat_tourism.data.auth.model.UserRole
import com.naranjapina.heat_tourism.features.auth.presentation.login.LogInScreen
import com.naranjapina.heat_tourism.features.auth.presentation.register.RegisterScreen
import com.naranjapina.heat_tourism.features.auth.presentation.restore.RestorePwdScreen
import com.naranjapina.heat_tourism.features.auth.presentation.splash.SplashScreen
import com.naranjapina.heat_tourism.features.company.presentation.Company.screen.CompanyScreen
import com.naranjapina.heat_tourism.features.home.presentation.Home.HomeDispatcher
import com.naranjapina.heat_tourism.features.map.presentation.Map.MapScreen
import com.naranjapina.heat_tourism.features.map.presentation.Map.RouteOverviewScreen
import com.naranjapina.heat_tourism.features.map.presentation.RouteMapScreen
import com.naranjapina.heat_tourism.features.route.presentation.Buy.BuyScreen
import com.naranjapina.heat_tourism.features.route.presentation.CreateRoute.CreateRouteScreen
import com.naranjapina.heat_tourism.features.route.presentation.Purchases.PurchasesScreen
import com.naranjapina.heat_tourism.features.route.presentation.Route.RouteScreen
import com.naranjapina.heat_tourism.features.settings.presentation.SettingsScreen
import com.naranjapina.heat_tourism.features.social.presentation.Chats.ChatScreen
import com.naranjapina.heat_tourism.features.social.presentation.Chats.ChatsListScreen
import com.naranjapina.heat_tourism.features.social.presentation.CreatePost.CreatePostScreen
import com.naranjapina.heat_tourism.features.social.presentation.Friend.FriendScreen
import com.naranjapina.heat_tourism.features.social.presentation.Notifications.NotificationsScreen
import com.naranjapina.heat_tourism.features.social.presentation.Post.PostScreen
import com.naranjapina.heat_tourism.features.social.presentation.Profile.ProfileScreen
import com.naranjapina.heat_tourism.features.social.presentation.Searcher.SearcherScreen
import com.naranjapina.heat_tourism.features.travel.presentation.CheckIn.CheckInScreen
import com.naranjapina.heat_tourism.shared.auth.AuthViewModel

/**
 * Rutas de navegacion organizadas por categoria.
 *
 * NOTA: las variantes "For Coordinator" / "For Administrator" NO son rutas separadas:
 * se manejan con condicionales segun user.tipo dentro de la misma pantalla.
 */
enum class Screen {
    // --- Auth ---
    Splash,
    Register,
    LogIn,
    RestorePwd,

    // --- Usuario general ---
    Home,
    Profile,
    Settings,
    Searcher,
    Map,                // Mapa de calor general (explorar destinos)
    Friend,             // Lista de amigos / contactos
    Notifications,
    CreatePost,
    Post,

    // --- Chats ---
    Chats,              // Lista de conversaciones
    Chat,               // Conversacion individual (recibe otherUserId)

    // --- Rutas y compras ---
    Route,              // Detalle de una ruta
    RouteOverview,      // Detalle + mapa de una ruta
    Buy,                // BuyRouteScreen
    Purchases,          // Historial de rutas compradas
    CreateRoute,        // CRUD de rutas (admin de empresa)

    // --- Viaje activo ---
    RouteMap,           // Mapa de la ruta activa en vivo (con grupo si es coordinador)
    CheckIn,            // Checkin de paradas

    // --- Empresa ---
    Company             // CompanyScreen (vista general / coord / admin segun usuario)
}

@Composable
fun NavigationStack() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.name
    ) {
        // ==================== AUTH ====================
        composable(Screen.Splash.name) {
            SplashScreen(
                authViewModel = authViewModel,
                onGoToHome = {
                    navController.navigate(Screen.Home.name) {
                        popUpTo(Screen.Splash.name) { inclusive = true }
                    }
                },
                onGoToLogIn = {
                    navController.navigate(Screen.LogIn.name) {
                        popUpTo(Screen.Splash.name) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.LogIn.name) {
            LogInScreen(
                authViewModel = authViewModel,
                viewModel = viewModel(),
                onGoToHome = {
                    navController.navigate(Screen.Home.name) {
                        popUpTo(Screen.LogIn.name) { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate(Screen.Register.name)
                },
                onGoToRestorePwd = {
                    navController.navigate(Screen.RestorePwd.name)
                }
            )
        }
        composable(Screen.Register.name) {
            RegisterScreen(
                authViewModel = authViewModel,
                viewModel = viewModel(),
                onGoToLogin = { navController.navigate(Screen.LogIn.name) },
                onGoToHome = {
                    navController.navigate(Screen.Home.name) {
                        popUpTo(Screen.Register.name) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.RestorePwd.name) {
            RestorePwdScreen(
                onGoBack = { navController.popBackStack() }
            )
        }

        // ==================== HOME (delega segun viaje activo o no) ====================
        composable(Screen.Home.name) {
            HomeDispatcher(navController)
        }

        // ==================== USUARIO GENERAL ====================
        composable(Screen.Profile.name) {
            ProfileScreen(
                authViewModel = authViewModel,
                navController = navController,
                onGoToCompany = {
                    navController.navigate(Screen.Company.name)
                }
            )
        }
        composable(Screen.Settings.name) {
            SettingsScreen(
                authViewModel = authViewModel,
                onGoBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Screen.LogIn.name) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Searcher.name) { SearcherScreen(navController) }
        composable(Screen.Map.name) { MapScreen(navController) }
        composable(Screen.Friend.name) { FriendScreen(navController) }
        composable(Screen.Notifications.name) { NotificationsScreen(navController) }
        composable(Screen.CreatePost.name) { CreatePostScreen(navController) }
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

        // ==================== CHATS ====================
        composable(Screen.Chats.name) { ChatsListScreen(navController) }
        composable(
            route = "${Screen.Chat.name}/{otherUserId}",
            arguments = listOf(
                navArgument("otherUserId") {
                    type = NavType.StringType
                }
            )
        ) {
            val otherUserId = it.arguments?.getString("otherUserId") ?: ""
            ChatScreen(navController, otherUserId)
        }

        // ==================== RUTAS Y COMPRAS ====================
        composable(Screen.Route.name) { RouteScreen(navController) }
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
        composable(Screen.Purchases.name) {
            PurchasesScreen(navController, authViewModel)
        }
        composable(Screen.CreateRoute.name) {
            CreateRouteScreen(navController)
        }

        // ==================== VIAJE ACTIVO ====================
        composable(Screen.RouteMap.name) {
            RouteMapScreen(
                groupId = "grupo123",
                userId = authState.user?.id ?: "unknown",
                isCoordinator = false
            )
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

        // ==================== EMPRESA ====================
        composable(
            route = "${Screen.Company.name}?companyId={companyId}",
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
    }
}
