package com.example.appfinanceiro.feature.login

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.appfinanceiro.core.biometric.BiometricAuth
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextSecondary
import com.example.appfinanceiro.core.network.auth.LoginRequest
import com.example.appfinanceiro.core.network.auth.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var erroLogin by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showBiometricOffer by remember { mutableStateOf(false) }
    var responseEmailForBiometric by remember { mutableStateOf("") }
    var pendingToken by remember { mutableStateOf("") }
    var pendingUserName by remember { mutableStateOf("") }
    var pendingUserEmail by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    val activity = LocalActivity.current as? FragmentActivity
    val savedToken by sessionManager.token.collectAsState(initial = null)
    val savedUserEmail by sessionManager.userEmail.collectAsState(initial = "")
    val biometricEnabledEmails by sessionManager.biometricEnabledEmails.collectAsState(initial = emptySet())

    val biometricEnabledForSavedUser =
        savedUserEmail.isNotBlank() &&
                biometricEnabledEmails.contains(savedUserEmail.trim().lowercase())

    val canUseBiometric = activity != null &&
            savedToken != null &&
            biometricEnabledForSavedUser &&
            BiometricAuth.isAvailable(activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(surfaceColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Logo", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Bem-vindo de volta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Faça login na sua conta para continuar", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "E-mail", color = textColor, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; erroLogin = false },
            placeholder = { Text("Digite o seu e-mail", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = surfaceColor, focusedContainerColor = surfaceColor,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue,
                focusedTextColor = textColor, unfocusedTextColor = textColor
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Senha", color = textColor, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it; erroLogin = false },
            placeholder = { Text("Digite a sua senha", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (senhaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(imageVector = image, contentDescription = "Mostrar senha", tint = TextSecondary)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = surfaceColor, focusedContainerColor = surfaceColor,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue,
                focusedTextColor = textColor, unfocusedTextColor = textColor
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !isLoading
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = onNavigateToForgot) {
                Text("Esqueci minha senha", color = PrimaryBlue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (erroLogin) {
            Text("Credenciais inválidas ou erro no servidor.", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (email.isNotEmpty() && senha.isNotEmpty()) {
                    coroutineScope.launch {
                        isLoading = true
                        erroLogin = false
                        try {
                            val response = RetrofitClient.authApi.login(LoginRequest(email, senha))

                            pendingToken = response.token
                            pendingUserName = response.user.name
                            pendingUserEmail = response.user.email
                            responseEmailForBiometric = response.user.email

                            if (activity != null && BiometricAuth.isAvailable(activity)) {
                                showBiometricOffer = true
                            } else {
                                sessionManager.saveToken(
                                    token = pendingToken,
                                    name = pendingUserName,
                                    email = pendingUserEmail
                                )
                                onLoginSuccess()
                            }


                        } catch (e: Exception) {

                            android.util.Log.e("API_ERRO", "Erro no login: ${e.message}")
                            erroLogin = true
                        }
                        finally {
                            isLoading = false
                        }
                    }
                } else {
                    erroLogin = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (canUseBiometric) {
            OutlinedButton(
                onClick = {
                    BiometricAuth.showBiometricPrompt(
                        activity = activity,
                        onSuccess = onLoginSuccess,
                        onError = { erroLogin = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Entrar com biometria")
            }
        }

        if (showBiometricOffer) {
            AlertDialog(
                onDismissRequest = { },
                containerColor = backgroundColor,
                titleContentColor = textColor,
                textContentColor = textColor,
                title = {
                    Text(
                        "Ativar biometria?",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Deseja usar biometria para deixar seu aplicativo mais seguro?",
                        color = textColor
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                sessionManager.saveToken(
                                    token = pendingToken,
                                    name = pendingUserName,
                                    email = pendingUserEmail
                                )

                                sessionManager.setBiometricEnabledForUser(
                                    email = pendingUserEmail,
                                    enabled = true
                                )

                                showBiometricOffer = false
                                onLoginSuccess()
                            }
                        }

                    ) {
                        Text(
                            "Ativar",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                sessionManager.saveToken(
                                    token = pendingToken,
                                    name = pendingUserName,
                                    email = pendingUserEmail
                                )

                                sessionManager.setBiometricEnabledForUser(
                                    email = pendingUserEmail,
                                    enabled = false
                                )

                                showBiometricOffer = false
                                onLoginSuccess()
                            }
                        }

                    ) {
                        Text(
                            "Agora não",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Não tem uma conta? ", color = TextSecondary)
            Text("Cadastre-se", color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigateToRegister() })
        }
    }
}