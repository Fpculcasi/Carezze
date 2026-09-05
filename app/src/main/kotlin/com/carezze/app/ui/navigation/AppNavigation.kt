package com.fpculcasi.carezze.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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

// ══════════════════════════════════════════════════════════════════════════════
// Route definitions — each @Serializable object/class becomes a type-safe route
// ══════════════════════════════════════════════════════════════════════════════

// -- Auth flow routes (outer NavHost) --
@Serializable object Welcome

@Serializable object Login

@Serializable object Register

// -- Main shell route: contains Scaffold + BottomBar + inner NavHost --
@Serializable object Main

// -- Bottom navigation tab routes (inner NavHost root destinations) --
@Serializable object Dashboard

@Serializable object PersonList

@Serializable object Profile

@Serializable object Settings

// -- Detail screen routes (inner NavHost, bottom bar hidden) --
@Serializable data class PersonDetail(val personId: String)

@Serializable data class EditPerson(val personId: String? = null)

@Serializable data class AddTherapy(val personId: String)

@Serializable data class TherapyDetail(val personId: String, val therapyId: String)

@Serializable data class HistoryList(val personId: String)

@Serializable data class HistoryCalendar(val personId: String)

// ══════════════════════════════════════════════════════════════════════════════
// Bottom navigation bar tab model
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Represents a single tab in the bottom navigation bar.
 * @param T The type-safe route type for this tab's start destination.
 */
data class BottomNavItem<T : Any>(
    val label: String,
    val icon: ImageVector,
    val route: T,
)

/** The four bottom navigation tabs displayed after authentication. */
val bottomNavItems =
    listOf(
        BottomNavItem(label = "Home", icon = Icons.Default.Home, route = Dashboard),
        BottomNavItem(label = "Persone", icon = Icons.Default.Face, route = PersonList),
        BottomNavItem(label = "Profilo", icon = Icons.Default.Person, route = Profile),
        BottomNavItem(label = "Impostazioni", icon = Icons.Default.Settings, route = Settings),
    )

// ══════════════════════════════════════════════════════════════════════════════
// Root navigation — handles authentication flow and the main shell
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Top-level composable that sets up the root [NavHost].
 *
 * Navigation graph:
 *   Welcome ──► Login ──► Register
 *       │         │          │
 *       └─────────┴──────────┘
 *                 │
 *                 ▼
 *               Main (Scaffold + inner NavHost)
 */
@Composable
fun AppNavigation() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = Welcome,
    ) {
        // -- Welcome / landing screen --
        composable<Welcome> {
            WelcomeScreen(
                onNavigateToDashboard = {
                    rootNavController.navigate(Main) {
                        // Clear the entire auth back stack so the user can't go back
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    rootNavController.navigate(Login)
                },
            )
        }

        // -- Login screen --
        composable<Login> {
            LoginScreen(
                onNavigateToDashboard = {
                    rootNavController.navigate(Main) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    rootNavController.navigate(Register)
                },
                onNavigateBack = {
                    rootNavController.popBackStack()
                },
            )
        }

        // -- Registration screen --
        composable<Register> {
            RegisterScreen(
                onNavigateToDashboard = {
                    rootNavController.navigate(Main) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    // Replace Register with Login on the back stack
                    rootNavController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    rootNavController.popBackStack()
                },
            )
        }

        // -- Main shell (post-auth) --
        composable<Main> {
            MainScreen()
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// Main shell — Scaffold with BottomBar and an inner NavHost for tab content
// ══════════════════════════════════════════════════════════════════════════════

/**
 * The main application shell displayed after successful authentication.
 *
 * Contains:
 * - A [NavigationBar] (bottom bar) with 4 tabs
 * - An inner [NavHost] for tab content and detail screens
 *
 * The bottom bar is only visible on root tab destinations; it hides
 * automatically when the user navigates to detail screens.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine whether to show the bottom bar:
    // only visible on the four root tab destinations
    val showBottomBar =
        bottomNavItems.any { item ->
            currentDestination?.hasRoute(item.route::class) == true
        }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            // Pop back to the graph's start destination to avoid
                            // building up a massive back stack when switching tabs
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore previously saved state for this tab
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        // ── Inner NavHost: tab content + detail screens ──
        NavHost(
            navController = navController,
            startDestination = Dashboard,
            modifier = Modifier.padding(innerPadding),
        ) {
            // ────────────────────────────────────────────
            // Tab root destinations (bottom bar visible)
            // ────────────────────────────────────────────

            composable<Dashboard> {
                DashboardScreen(
                    onNavigateToSettings = { navController.navigate(Settings) },
                    onNavigateToPersons = { navController.navigate(PersonList) },
                    onNavigateToHistory = { personId ->
                        navController.navigate(HistoryList(personId))
                    },
                )
            }

            composable<PersonList> {
                PersonListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(EditPerson()) },
                    onNavigateToPerson = { id ->
                        navController.navigate(PersonDetail(id))
                    },
                )
            }

            composable<Profile> {
                // Placeholder: la schermata Profilo/Account arriva con il rework UX (vedi 06-implementation-plan)
                Text("Profilo")
            }

            composable<Settings> {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            // ────────────────────────────────────────────
            // Detail screens (bottom bar hidden)
            // ────────────────────────────────────────────

            composable<PersonDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<PersonDetail>()
                PersonDetailScreen(
                    personId = route.personId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(EditPerson(id))
                    },
                    onNavigateToAddTherapy = { pid ->
                        navController.navigate(AddTherapy(pid))
                    },
                    onNavigateToTherapy = { pid, tid ->
                        navController.navigate(TherapyDetail(pid, tid))
                    },
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
                    onNavigateToCalendar = {
                        navController.navigate(HistoryCalendar(route.personId))
                    },
                )
            }

            composable<HistoryCalendar> {
                HistoryCalendarScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// Extracted BottomNavigationBar composable for cleaner separation
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Material 3 [NavigationBar] with 4 tabs.
 *
 * Uses [NavDestination.hierarchy] to correctly highlight the active tab
 * even when on a nested destination within that tab's navigation graph.
 *
 * @param currentDestination The currently active navigation destination.
 * @param onTabSelected Callback invoked with the tab's route when tapped.
 */
@Composable
private fun BottomNavigationBar(
    currentDestination: androidx.navigation.NavDestination?,
    onTabSelected: (Any) -> Unit,
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            // Check whether this tab is selected by walking the destination hierarchy;
            // this ensures correct highlighting for nested detail screens as well
            val selected =
                currentDestination?.hierarchy?.any {
                    it.hasRoute(item.route::class)
                } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(text = item.label) },
            )
        }
    }
}
