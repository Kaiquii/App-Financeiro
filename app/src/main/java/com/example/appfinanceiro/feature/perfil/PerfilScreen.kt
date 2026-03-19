package com.example.appfinanceiro.feature.perfil

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.components.ExitConfirmationDialog
import com.example.appfinanceiro.core.designsystem.components.StandardBottomBar
import com.example.appfinanceiro.core.designsystem.theme.DangerRed
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onLogoutClick: () -> Unit = {},
    onNavigate: (Int) -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = {
                showExitDialog = false
                onLogoutClick()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = textColor, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            StandardBottomBar(
                itemSelecionado = 3,
                onItemClick = onNavigate,
                onAddClick = { /* TODO: Abrir tela de nova despesa */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(PrimaryBlue.copy(alpha = 0.2f), CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Foto de perfil", tint = PrimaryBlue, modifier = Modifier.size(60.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Kaiqui Lucas", color = textColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("kaiqui.lucaskaiquiluc@email.com", color = TextMuted, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { /* TODO: Editar Perfil */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.5f))
            ) {
                Text("Editar Perfil")
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("Configurações")
            SettingsItem(icon = Icons.Default.AccountBalance, iconColor = PrimaryBlue, title = "Configurações de Renda", subtitle = "Salário, Adiantamento")
            SettingsItem(icon = Icons.Default.Notifications, iconColor = PrimaryBlue, title = "Notificações", subtitle = "Alertas de gastos, lembretes")
            SettingsItem(icon = Icons.Default.Lock, iconColor = PrimaryBlue, title = "Segurança", subtitle = "Senha, Face ID, Biometria")
            SettingsItem(icon = Icons.Default.Download, iconColor = PrimaryBlue, title = "Exportar Dados", subtitle = "Baixar relatórios em PDF/CSV")

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Ajuda")
            SettingsItem(icon = Icons.Default.HelpOutline, iconColor = PrimaryBlue, title = "Central de Ajuda", subtitle = "Dúvidas frequentes e suporte")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { showExitDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Sair", tint = DangerRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da Conta", color = DangerRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Versão 2.4.1 (104)", color = TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}

@Composable
private fun SettingsItem(icon: ImageVector, iconColor: Color, title: String, subtitle: String) {
    val cardBg = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(cardBg, RoundedCornerShape(12.dp))
            .clickable { /* TODO: Ação do item */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }

        Icon(Icons.Default.ChevronRight, contentDescription = "Acessar", tint = TextMuted)
    }
}