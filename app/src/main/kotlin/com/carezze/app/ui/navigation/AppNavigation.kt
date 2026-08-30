package com.fpculcasi.carezze.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fpculcasi.carezze.ui.auth.LoginScreen
import com.fpculcasi.carezze.ui.auth.RegisterScreen
import com.fpculcasi.carezze.ui.auth.WelcomeScreen
import com.fpculcasi.carezze.ui.dashboard.DashboardScreen
import com.fpculcasi.carezze.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object Welcome

@Serializable
object Dashboard

@Serializable
object Login

@Serializable
object Register

@Serializable
object Settings

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Welcome,
    ) {
        composable<Welcome> {
            WelcomeScreen(
                onNavigateToDashboard = {
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Login) },
            )
        }

        composable<Dashboard> {
            DashboardScreen(onNavigateToSettings = { navController.navigate(Settings) })
        }

        composable<Settings> {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<Login> {
            LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Register) },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateToDashboard = {
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
