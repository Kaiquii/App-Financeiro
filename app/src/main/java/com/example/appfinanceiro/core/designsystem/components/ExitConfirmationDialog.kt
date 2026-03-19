package com.example.appfinanceiro.core.designsystem.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.appfinanceiro.core.designsystem.theme.BackgroundDark
import com.example.appfinanceiro.core.designsystem.theme.BackgroundLight
import com.example.appfinanceiro.core.designsystem.theme.DangerRed
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) BackgroundDark else BackgroundLight
    val textColor = if (isDark) Color.White else Color.Black

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = backgroundColor,
        titleContentColor = textColor,
        textContentColor = textColor,
        title = {
            Text(text = "Sair do Sistema", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "Tem certeza que deseja sair do sistema?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sim", color = DangerRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = PrimaryBlue)
            }
        }
    )
}