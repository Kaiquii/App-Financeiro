package com.example.appfinanceiro.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.theme.*
import com.example.appfinanceiro.core.network.home.utils.formatCurrency

@Composable
fun ResumoFinanceiroSection(
    isLoading: Boolean,
    salario: Double,
    adiantamento: Double,
    rendaExtra: Double,
    restSalario: Double,
    restAdiant: Double,
    totalDisp: Double
) {
    val isDark = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = MaterialTheme.colorScheme.surface
    val cardPurple = if (isDark) SurfaceCardPurple else SurfaceCardPurpleLight
    val cardBlue = if (isDark) SurfaceCardBlue else SurfaceCardBlueLight

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Resumo Financeiro", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (isLoading) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimaryBlue, strokeWidth = 2.dp)
                }
            }
            Text("Editar", color = PrimaryBlue, fontSize = 14.sp, modifier = Modifier.clickable { /* TODO */ })
        }

        Column(modifier = Modifier.alpha(if (isLoading) 0.5f else 1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(title = "Salário", value = formatCurrency(salario), bgColor = cardBg, valueColor = textColor, modifier = Modifier.weight(1f))
                SummaryCard(title = "Adiantamento", value = formatCurrency(adiantamento), bgColor = cardBg, valueColor = textColor, modifier = Modifier.weight(1f))
            }
            SummaryCard(title = "Renda Extra", value = "+ ${formatCurrency(rendaExtra)}", bgColor = cardBg, valueColor = GreenPositive, icon = Icons.Default.TrendingUp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(title = "Restante Salário", value = formatCurrency(restSalario), bgColor = cardPurple, valueColor = textColor, modifier = Modifier.weight(1f))
                SummaryCard(title = "Restante Adiant.", value = formatCurrency(restAdiant), bgColor = cardPurple, valueColor = textColor, modifier = Modifier.weight(1f))
            }
            SummaryCard(title = "Total Geral Disponível", value = formatCurrency(totalDisp), valueSize = 24.sp, valueColor = PrimaryBlue, bgColor = cardBlue, icon = Icons.Default.AccountBalanceWallet)
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier, bgColor: Color, valueColor: Color, valueSize: androidx.compose.ui.unit.TextUnit = 18.sp, icon: ImageVector? = null) {
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