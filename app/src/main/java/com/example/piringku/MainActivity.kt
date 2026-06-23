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
import com.example.piringku.ui.theme.PIRINGKUTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PIRINGKUTheme() {
                MainNavigationStructure()
            }
        }
    }
}

// 1. Definisikan rute dan ikon untuk menu bawah
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Journal : Screen("journal", "Journal", Icons.Default.DateRange)
    object Stats : Screen("stats", "Stats", Icons.Default.Share) // Sementara pakai ikon Share (mirip grafik)
    object Cari : Screen("cari", "Cari", Icons.Default.Search)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
}

@Composable
fun MainNavigationStructure() {
    val navController = rememberNavController()
    val items = listOf(Screen.Journal, Screen.Stats, Screen.Cari, Screen.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
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
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 2. Mengatur perpindahan halaman saat menu diklik
        NavHost(
            navController = navController,
            startDestination = Screen.Journal.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Journal.route) { JournalScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Cari.route) { SearchScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}