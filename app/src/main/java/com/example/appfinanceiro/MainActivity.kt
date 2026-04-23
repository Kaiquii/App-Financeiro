package com.example.appfinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.theme.AppFinanceiroTheme
import com.example.appfinanceiro.feature.despesas.DespesasScreen
import com.example.appfinanceiro.feature.despesas.components.EditarDespesaScreen
import com.example.appfinanceiro.feature.despesas.components.NovaDespesaScreen
import com.example.appfinanceiro.feature.home.HomeScreen
import com.example.appfinanceiro.feature.login.LoginScreen
import com.example.appfinanceiro.feature.perfil.PerfilScreen
import com.example.appfinanceiro.feature.login.RegisterScreen
import com.example.appfinanceiro.feature.login.EsqueciSenhaScreen
import com.example.appfinanceiro.feature.perfil.components.CategoriasScreen
import com.example.appfinanceiro.feature.perfil.components.ConfiguracoesRendaScreen
import com.example.appfinanceiro.feature.perfil.components.EditarPerfilScreen
import com.example.appfinanceiro.feature.relatorios.RelatoriosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)

        setContent {
            val userToken by sessionManager.token.collectAsState(initial = null)

            AppFinanceiroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val destination = if (userToken != null) "home" else "login"

                    NavHost(navController = navController, startDestination = destination) {

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                },
                                onNavigateToForgot = {
                                    navController.navigate("esqueci_senha")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onRegisterSuccess = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("esqueci_senha") {
                            EsqueciSenhaScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigate = { tabIndex ->
                                    when (tabIndex) {
                                        1 -> navController.navigate("despesas") { launchSingleTop = true }
                                        2 -> navController.navigate("relatorios") { launchSingleTop = true }
                                        3 -> navController.navigate("perfil") { launchSingleTop = true }
                                    }
                                },
                                onAddClick = {
                                    navController.navigate("nova_despesa")
                                }
                            )
                        }

                        composable("perfil") {
                            PerfilScreen(
                                onLogoutClick = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigate = { tabIndex ->
                                    when (tabIndex) {
                                        0 -> navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                        1 -> navController.navigate("despesas") { launchSingleTop = true }
                                        2 -> navController.navigate("relatorios") { launchSingleTop = true }
                                    }
                                },
                                onIncomeSettingsClick = {
                                    navController.navigate("configuracoes_renda")
                                },
                                onCategoriesClick = {
                                    navController.navigate("categorias")
                                },
                                onEditProfileClick = {
                                    navController.navigate("editar_perfil")
                                }
                            )
                        }


                        composable("nova_despesa") {
                            NovaDespesaScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("despesas") {
                            DespesasScreen(
                                onNavigate = { tabIndex ->
                                    when (tabIndex) {
                                        0 -> navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                        2 -> navController.navigate("relatorios") { launchSingleTop = true }
                                        3 -> navController.navigate("perfil") { launchSingleTop = true }
                                    }
                                },
                                onAddClick = {
                                    navController.navigate("nova_despesa")
                                },
                                onEditClick = { expenseId ->
                                    navController.navigate("editar_despesa/$expenseId")
                                }
                            )
                        }

                        composable("editar_despesa/{expenseId}") { backStackEntry ->
                            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toIntOrNull()
                            if (expenseId != null) {
                                EditarDespesaScreen(
                                    expenseId = expenseId,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }

                        composable("configuracoes_renda") {
                            ConfiguracoesRendaScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("categorias") {
                            CategoriasScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("editar_perfil") {
                            EditarPerfilScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("relatorios") {
                            RelatoriosScreen(
                                onNavigate = { tabIndex ->
                                    when (tabIndex) {
                                        0 -> navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                        1 -> navController.navigate("despesas") { launchSingleTop = true }
                                        3 -> navController.navigate("perfil") { launchSingleTop = true }
                                    }
                                },
                                onAddClick = {
                                    navController.navigate("nova_despesa")
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}