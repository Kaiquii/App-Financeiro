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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.theme.*
import com.example.appfinanceiro.core.network.SummaryResponse
import com.example.appfinanceiro.feature.home.utils.formatCurrency

@Composable
fun ResumoFinanceiroSection(isLoading: Boolean, data: SummaryResponse?) {
    val isDark = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = MaterialTheme.colorScheme.surface
    val cardPurple = if (isDark) SurfaceCardPurple else SurfaceCardPurpleLight
    val cardBlue = if (isDark) SurfaceCardBlue else SurfaceCardBlueLight
    val alertColor = Color(0xFFFF6B6B)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Resumo Financeiro",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isLoading && data != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = PrimaryBlue,
                        strokeWidth = 2.dp
                    )
                }
            }
            Text(
                "Editar",
                color = PrimaryBlue,
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        if (data == null && isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier.alpha(if (isLoading) 0.5f else 1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val salario = data?.salario ?: 0.0
                val adiantamento = data?.adiantamento ?: 0.0
                val rendaExtraDisp = data?.restante_renda_extra ?: 0.0

                val gastoSalario = data?.total_gasto_salario ?: 0.0
                val gastoAdiant = data?.total_gasto_adiantamento ?: 0.0

                val restSalario = data?.restante_salario ?: 0.0
                val restAdiant = data?.restante_adiantamento ?: 0.0

                val totalRecebido = data?.total_income ?: 0.0
                val totalGasto = data?.total_expense ?: 0.0
                val totalDisp = data?.total_geral_disponivel ?: 0.0

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(
                        title = "Salário",
                        value = formatCurrency(salario),
                        bgColor = cardBg,
                        valueColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Adiantamento",
                        value = formatCurrency(adiantamento),
                        bgColor = cardBg,
                        valueColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                SummaryCard(
                    title = "Renda Extra (Disponível)",
                    value = formatCurrency(rendaExtraDisp),
                    bgColor = cardBg,
                    valueColor = GreenPositive,
                    icon = Icons.Default.TrendingUp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(
                        title = "Restante Salário",
                        value = formatCurrency(restSalario),
                        bgColor = cardPurple,
                        valueColor = if (restSalario < 0) alertColor else textColor,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Restante Adiant.",
                        value = formatCurrency(restAdiant),
                        bgColor = cardPurple,
                        valueColor = if (restAdiant < 0) alertColor else textColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(
                        title = "Gasto Salário",
                        value = formatCurrency(gastoSalario),
                        bgColor = cardBg,
                        valueColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Gasto Adiant.",
                        value = formatCurrency(gastoAdiant),
                        bgColor = cardBg,
                        valueColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(
                        title = "Total Recebido",
                        value = formatCurrency(totalRecebido),
                        bgColor = cardBg,
                        valueColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Total Gasto",
                        value = formatCurrency(totalGasto),
                        bgColor = cardBg,
                        valueColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                SummaryCard(
                    title = "Total Geral Disponível",
                    value = formatCurrency(totalDisp),
                    valueSize = 24.sp,
                    valueColor = if (totalDisp < 0) alertColor else PrimaryBlue,
                    bgColor = cardBlue,
                    icon = Icons.Default.AccountBalanceWallet
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    bgColor: Color,
    valueColor: Color,
    valueSize: TextUnit = 18.sp,
    icon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    value,
                    color = valueColor,
                    fontSize = valueSize,
                    fontWeight = FontWeight.Bold
                )
                if (icon != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = valueColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
