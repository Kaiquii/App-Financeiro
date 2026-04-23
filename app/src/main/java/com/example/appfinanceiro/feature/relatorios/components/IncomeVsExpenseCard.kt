package com.example.appfinanceiro.feature.relatorios.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.theme.DangerRed
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextMuted
import com.example.appfinanceiro.core.network.ChartReportResponse
import com.example.appfinanceiro.core.network.SummaryResponse
import com.example.appfinanceiro.feature.relatorios.ReportRange
import com.example.appfinanceiro.feature.relatorios.utils.formatCurrency

@Composable
fun IncomeVsExpenseCard(
    summaryData: SummaryResponse?,
    chartData: List<ChartReportResponse>,
    selectedRange: ReportRange,
    currentMonth: Int,
    onRangeSelected: (ReportRange) -> Unit
) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = androidx.compose.ui.Modifier.padding(18.dp)) {
            Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Renda vs Despesas",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = androidx.compose.ui.Modifier.height(6.dp))
                    Text(
                        text = "Saldo: ${formatCurrency(summaryData?.total_geral_disponivel ?: 0.0)}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    LegendItem("Renda", PrimaryBlue)
                    Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
                    LegendItem("Despesa", DangerRed)
                }
            }

            Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

            ReportRangeSelector(
                selectedRange = selectedRange,
                onRangeSelected = onRangeSelected
            )

            Spacer(modifier = androidx.compose.ui.Modifier.height(20.dp))

            if (chartData.isEmpty()) {
                Text(
                    text = "Sem dados do gráfico.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            } else {
                ReportsBarChart(
                    data = chartData,
                    selectedRange = selectedRange,
                    currentMonth = currentMonth
                )
            }
        }
    }
}
