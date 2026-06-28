package com.example.piringku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.piringku.ui.auth.DataDiriScreen
import com.example.piringku.ui.auth.LoginScreen
import com.example.piringku.ui.auth.RegisterScreen
import com.example.piringku.ui.barcode.BarcodeScanner
import com.example.piringku.ui.journal.JournalScreen
import com.example.piringku.ui.profile.ProfilScreen
import com.example.piringku.ui.search.SearchScreen
import com.example.piringku.ui.settings.ReminderSettingsScreen
import com.example.piringku.ui.splash.SplashScreen
import com.example.piringku.ui.stats.ProgresGoalsScreen
import com.example.piringku.ui.stats.StatsScreen
import com.example.piringku.ui.theme.PIRINGKUTheme
import com.example.piringku.util.MealReminderScheduler
import com.example.piringku.util.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        MealReminderScheduler.scheduleAll(this)
        setContent {
            PIRINGKUTheme() {
                PiringkuApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object DataDiri : Screen("data_diri")
    object Journal : Screen("journal", "Journal", Icons.Default.DateRange)
    object Stats : Screen("stats", "Stats", Icons.Default.Share)
    object Cari : Screen("cari", "Cari", Icons.Default.Search)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
    object Barcode : Screen("barcode")
    object ProgresGoals : Screen("progres_goals")
    object EditDataDiri : Screen("edit_data_diri")
    object ReminderSettings : Screen("reminder_settings")
}

private val bottomNavItems = listOf(Screen.Journal, Screen.Stats, Screen.Cari, Screen.Profile)
private val authRoutes = setOf(Screen.Splash.route, Screen.Login.route, Screen.Register.route, Screen.DataDiri.route)
private val fullscreenRoutes = setOf(Screen.Barcode.route, Screen.ProgresGoals.route, Screen.EditDataDiri.route, Screen.ReminderSettings.route)

@Composable
fun PiringkuApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != null &&
        currentRoute !in authRoutes &&
        currentRoute !in fullscreenRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                    onNavigateToMain = { navController.navigate(Screen.Journal.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = { navController.navigate(Screen.DataDiri.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegistered = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                )
            }
            composable(Screen.DataDiri.route) {
                DataDiriScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.navigate(Screen.Journal.route) { popUpTo(Screen.DataDiri.route) { inclusive = true } } },
                )
            }
            composable(Screen.Journal.route) { JournalScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Cari.route) {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToBarcode = { navController.navigate(Screen.Barcode.route) },
                )
            }
            composable(Screen.Profile.route) {
                ProfilScreen(
                    onEditDataDiri = { navController.navigate(Screen.EditDataDiri.route) },
                    onProgresGoals = { navController.navigate(Screen.ProgresGoals.route) },
                    onReminderSettings = { navController.navigate(Screen.ReminderSettings.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.Barcode.route) {
                BarcodeScanner(
                    onBack = { navController.popBackStack() },
                    onBarcodeScanned = { navController.popBackStack() },
                    onManualSearch = { navController.popBackStack() },
                )
            }
            composable(Screen.ProgresGoals.route) {
                ProgresGoalsScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Screen.EditDataDiri.route) {
                DataDiriScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable(Screen.ReminderSettings.route) {
                ReminderSettingsScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }

}
