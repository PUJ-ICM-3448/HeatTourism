package com.naranjapina.heat_tourism.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.naranjapina.heat_tourism.features.auth.presentation.login.LogInScreen
import com.naranjapina.heat_tourism.features.auth.presentation.register.RegisterScreen
import com.naranjapina.heat_tourism.features.auth.presentation.restore.RestorePwdScreen
import com.naranjapina.heat_tourism.features.auth.presentation.splash.SplashScreen
import com.naranjapina.heat_tourism.features.company.presentation.CreateRoute.CreateRouteScreen
import com.naranjapina.heat_tourism.features.company.presentation.ManageCompany.ManageCompanyScreen
import com.naranjapina.heat_tourism.features.home.presentation.Home.NoTravelHomeScreen
import com.naranjapina.heat_tourism.features.home.presentation.Home.TravelHomeScreen
import com.naranjapina.heat_tourism.features.map.presentation.Map.MapScreen
import com.naranjapina.heat_tourism.features.map.presentation.Map.RouteOverviewScreen
import com.naranjapina.heat_tourism.features.map.presentation.RouteMap.RouteMapScreen
import com.naranjapina.heat_tourism.features.route.presentation.Buy.BuyScreen
import com.naranjapina.heat_tourism.features.route.presentation.Purchases.PurchasesScreen
import com.naranjapina.heat_tourism.features.settings.presentation.SettingsScreen
import com.naranjapina.heat_tourism.features.social.presentation.Chats.ChatScreen
import com.naranjapina.heat_tourism.features.social.presentation.Chats.ChatsListScreen
import com.naranjapina.heat_tourism.features.social.presentation.Company.CompanyScreen
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
 *
 * Pantallas eliminadas vs Entrega 2:
 * - Route.kt (fusionada en RouteOverview que ya tiene mapa + descripcion).
 * - RouteGroup.kt (estaba vacia, la logica del coord vive en TravelHome con condicional).
 * - LogInCoordinator.kt (un solo login, rol detectado por user.tipo).
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
    RouteOverview,      // Detalle + mapa de una ruta (antes Route + RouteOverview, ahora fusionado)
    Buy,                // BuyRouteScreen
    Purchases,          // Historial de rutas compradas

    // --- Viaje activo ---
    RouteMap,           // Mapa de la ruta activa en vivo (con grupo si es coordinador)
    CheckIn,            // Checkin de paradas

    // --- Empresa / Coordinador ---
    Company,            // CompanyScreen (3 modos: general / coord / admin)
    ManageCompany,      // CompanyScreen For Administrator (a fusionar con Company)
    CreateRoute         // CRUD de rutas (mock segun grafo)
}

@Composable
fun NavigationStack() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

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

        // ==================== HOME (usuario general / viaje activo) ====================
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
            if (state == "travel") TravelHomeScreen(navController)
            else NoTravelHomeScreen(navController)
        }

        // ==================== USUARIO GENERAL ====================
        composable(Screen.Profile.name) {
            ProfileScreen(authViewModel, navController)
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
        composable(Screen.Post.name) { PostScreen(navController) }

        // ==================== CHATS ====================
        composable(Screen.Chats.name) { ChatsListScreen(navController) }
        composable(
            route = "${Screen.Chat.name}?otherUserId={otherUserId}",
            arguments = listOf(
                navArgument("otherUserId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val otherUserId = it.arguments?.getString("otherUserId") ?: ""
            ChatScreen(navController, otherUserId)
        }

        // ==================== RUTAS Y COMPRAS ====================
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
        composable(Screen.Buy.name) { BuyScreen(navController) }
        composable(Screen.Purchases.name) { PurchasesScreen(navController) }

        // ==================== VIAJE ACTIVO ====================
        composable(Screen.RouteMap.name) { RouteMapScreen(navController) }
        composable(Screen.CheckIn.name) { CheckInScreen(navController) }

        // ==================== EMPRESA / COORDINADOR ====================
        composable(Screen.Company.name) { CompanyScreen(navController) }
        composable(Screen.ManageCompany.name) { ManageCompanyScreen(navController) }
        composable(Screen.CreateRoute.name) { CreateRouteScreen(navController) }
    }
}
