package com.example.appfinanceiro

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appfinanceiro.core.designsystem.theme.BackgroundDark
import com.example.appfinanceiro.core.designsystem.theme.BackgroundLight
import com.example.appfinanceiro.feature.home.HomeScreen
import com.example.appfinanceiro.feature.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark = isSystemInDarkTheme()
            val bgColor = if (isDark) BackgroundDark else BackgroundLight

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    window.statusBarColor = bgColor.toArgb()
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = bgColor
            ) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            onLogoutClick = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}