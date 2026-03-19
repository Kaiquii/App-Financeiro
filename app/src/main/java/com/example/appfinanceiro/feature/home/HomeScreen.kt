package com.example.appfinanceiro.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.components.ExitConfirmationDialog
import com.example.appfinanceiro.core.designsystem.components.StandardBottomBar
import com.example.appfinanceiro.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogoutClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) BackgroundDark else BackgroundLight
    val textColor = if (isDark) Color.White else Color.Black

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

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
                title = { Text("Visão Mensal", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Voltar */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            StandardBottomBar(
                itemSelecionado = 0,
                onAddClick = { /* TODO: Abrir tela de nova despesa */ }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MonthSelector() }
            item { ResumoFinanceiroSection() }
            item { DespesasSection() }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun MonthSelector() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = TextMuted)
        Text("Fevereiro", color = TextMuted)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "Mês atual", tint = PrimaryBlue)
            Text("Março", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.width(40.dp).padding(top = 4.dp), color = PrimaryBlue, thickness = 2.dp)
        }

        Text("Abril", color = TextMuted)
        Icon(Icons.Default.ChevronRight, contentDescription = "Próximo", tint = TextMuted)
    }
}

@Composable
private fun ResumoFinanceiroSection() {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black
    val cardBg = if (isDark) SurfaceCard else SurfaceCardLight
    val cardPurple = if (isDark) SurfaceCardPurple else SurfaceCardPurpleLight
    val cardBlue = if (isDark) SurfaceCardBlue else SurfaceCardBlueLight

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Resumo Financeiro", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Editar", color = PrimaryBlue, fontSize = 14.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(title = "Salário", value = "R$ 5.200,00", bgColor = cardBg, valueColor = textColor, modifier = Modifier.weight(1f))
            SummaryCard(title = "Adiantamento", value = "R$ 1.500,00", bgColor = cardBg, valueColor = textColor, modifier = Modifier.weight(1f))
        }

        SummaryCard(title = "Renda Extra", value = "+ R$ 450,00", bgColor = cardBg, valueColor = GreenPositive, icon = Icons.Default.TrendingUp)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(title = "Restante Salário", value = "R$ 3.400,00", bgColor = cardPurple, valueColor = textColor, modifier = Modifier.weight(1f))
            SummaryCard(title = "Restante Adiant.", value = "R$ 750,00", bgColor = cardPurple, valueColor = textColor, modifier = Modifier.weight(1f))
        }

        SummaryCard(title = "Total Geral Disponível", value = "R$ 4.600,00", valueSize = 24.sp, valueColor = PrimaryBlue, bgColor = cardBlue, icon = Icons.Default.AccountBalanceWallet)
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    bgColor: Color,
    valueColor: Color,
    valueSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    icon: ImageVector? = null
) {
    Box(modifier = modifier.background(bgColor, RoundedCornerShape(12.dp)).padding(16.dp)) {
        Column {
            Text(title, color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, color = valueColor, fontSize = valueSize, fontWeight = FontWeight.Bold)
                if (icon != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(icon, contentDescription = null, tint = valueColor, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DespesasSection() {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black
    val cardBg = if (isDark) SurfaceCard else SurfaceCardLight

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Despesas", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.background(cardBg, RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrar", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Filtrar", color = PrimaryBlue, fontSize = 12.sp)
            }
        }

        ExpenseItem(icon = Icons.Default.Home, iconColor = DangerRed, title = "Aluguel", type = "Fixa", date = "05/03", value = "- R$ 1.800,00")
        ExpenseItem(icon = Icons.Default.ShoppingCart, iconColor = PrimaryBlue, title = "Supermercado", type = "Única", date = "12/03", value = "- R$ 650,40")
        ExpenseItem(icon = Icons.Default.PhoneIphone, iconColor = Color(0xFF9C27B0), title = "Celular Novo", type = "Parc. 3/12", date = "15/03", value = "- R$ 350,00")
        ExpenseItem(icon = Icons.Default.FlashOn, iconColor = Color(0xFFFF9800), title = "Energia Elétrica", type = "Variável", date = "20/03", value = "- R$ 210,50")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TextMuted.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = "Adicionar", tint = textColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Despesa", color = textColor)
            }
        }
    }
}

@Composable
private fun ExpenseItem(icon: ImageVector, iconColor: Color, title: String, type: String, date: String, value: String) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black
    val cardBg = if (isDark) SurfaceCard else SurfaceCardLight

    Row(
        modifier = Modifier.fillMaxWidth().background(cardBg, RoundedCornerShape(12.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.background(TextMuted.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(type, color = TextMuted, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("• $date", color = TextMuted, fontSize = 10.sp)
            }
        }
        Text(value, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}