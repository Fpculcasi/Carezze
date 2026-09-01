package com.fpculcasi.carezze.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fpculcasi.carezze.ui.auth.LoginScreen
import com.fpculcasi.carezze.ui.auth.RegisterScreen
import com.fpculcasi.carezze.ui.auth.WelcomeScreen
import com.fpculcasi.carezze.ui.dashboard.DashboardScreen
import com.fpculcasi.carezze.ui.history.HistoryCalendarScreen
import com.fpculcasi.carezze.ui.history.HistoryListScreen
import com.fpculcasi.carezze.ui.person.EditPersonScreen
import com.fpculcasi.carezze.ui.person.PersonDetailScreen
import com.fpculcasi.carezze.ui.person.PersonListScreen
import com.fpculcasi.carezze.ui.settings.SettingsScreen
import com.fpculcasi.carezze.ui.therapy.AddTherapyScreen
import com.fpculcasi.carezze.ui.therapy.TherapyDetailScreen
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

@Serializable
object PersonList

@Serializable
data class PersonDetail(val personId: String)

@Serializable
data class EditPerson(val personId: String? = null)

@Serializable
data class AddTherapy(val personId: String)

@Serializable
data class TherapyDetail(val personId: String, val therapyId: String)

@Serializable
data class HistoryList(val personId: String)

@Serializable
data class HistoryCalendar(val personId: String)

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
            DashboardScreen(
                onNavigateToSettings = { navController.navigate(Settings) },
                onNavigateToPersons = { navController.navigate(PersonList) },
                onNavigateToHistory = { personId -> navController.navigate(HistoryList(personId)) },
            )
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

        composable<PersonList> {
            PersonListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(EditPerson()) },
                onNavigateToPerson = { id -> navController.navigate(PersonDetail(id)) },
            )
        }

        composable<PersonDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<PersonDetail>()
            PersonDetailScreen(
                personId = route.personId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(EditPerson(id)) },
                onNavigateToAddTherapy = { pid -> navController.navigate(AddTherapy(pid)) },
                onNavigateToTherapy = { pid, tid -> navController.navigate(TherapyDetail(pid, tid)) },
            )
        }

        composable<EditPerson> { backStackEntry ->
            val route = backStackEntry.toRoute<EditPerson>()
            EditPersonScreen(
                personId = route.personId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<AddTherapy> { backStackEntry ->
            val route = backStackEntry.toRoute<AddTherapy>()
            AddTherapyScreen(
                personId = route.personId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<TherapyDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TherapyDetail>()
            TherapyDetailScreen(
                personId = route.personId,
                therapyId = route.therapyId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<HistoryList> { backStackEntry ->
            val route = backStackEntry.toRoute<HistoryList>()
            HistoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCalendar = { navController.navigate(HistoryCalendar(route.personId)) },
            )
        }

        composable<HistoryCalendar> {
            HistoryCalendarScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
