package com.example.appfinanceiro.feature.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextSecondary
import com.example.appfinanceiro.core.network.auth.RetrofitClient
import com.example.appfinanceiro.core.network.auth.ResetPasswordRequest
import kotlinx.coroutines.launch

@Composable
fun EsqueciSenhaScreen(
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var senhaAntiga by remember { mutableStateOf("") }
    var senhaNova by remember { mutableStateOf("") }

    var senhaAntigaVisivel by remember { mutableStateOf(false) }
    var senhaNovaVisivel by remember { mutableStateOf(false) }

    var erro by remember { mutableStateOf(false) }
    var mensagemErro by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.TopStart).offset(x = (-12).dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = textColor)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(surfaceColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.LockReset, contentDescription = "Recuperar", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Recuperar Senha", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Atualize sua senha de acesso", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "E-mail", color = textColor, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; erro = false },
                placeholder = { Text("seu@email.com", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = surfaceColor, focusedContainerColor = surfaceColor,
                    unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue,
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true, enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Senha Antiga", color = textColor, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = senhaAntiga,
                onValueChange = { senhaAntiga = it; erro = false },
                placeholder = { Text("Digite a senha antiga", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (senhaAntigaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (senhaAntigaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { senhaAntigaVisivel = !senhaAntigaVisivel }) { Icon(image, contentDescription = null, tint = TextSecondary) }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = surfaceColor, focusedContainerColor = surfaceColor,
                    unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue,
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true, enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Nova Senha", color = textColor, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = senhaNova,
                onValueChange = { senhaNova = it; erro = false },
                placeholder = { Text("Crie a nova senha", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (senhaNovaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (senhaNovaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { senhaNovaVisivel = !senhaNovaVisivel }) { Icon(image, contentDescription = null, tint = TextSecondary) }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = surfaceColor, focusedContainerColor = surfaceColor,
                    unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue,
                    focusedTextColor = textColor, unfocusedTextColor = textColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true, enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (erro) {
                Text(mensagemErro, color = MaterialTheme.colorScheme.error, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (email.isNotEmpty() && senhaAntiga.isNotEmpty() && senhaNova.isNotEmpty()) {
                        coroutineScope.launch {
                            isLoading = true
                            erro = false
                            try {
                                val response = RetrofitClient.authApi.resetPassword(
                                    ResetPasswordRequest(email, senhaAntiga, senhaNova)
                                )
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                onNavigateBack()
                            } catch (e: Exception) {
                                erro = true
                                mensagemErro = "Erro ao atualizar. Verifique os dados informados."
                                android.util.Log.e("API_ERRO", "Falha na atualização", e)
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        erro = true
                        mensagemErro = "Preencha todos os campos."
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
                    Text("Atualizar Senha", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}