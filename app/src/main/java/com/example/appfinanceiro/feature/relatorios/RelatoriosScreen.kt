package com.example.appfinanceiro.feature.relatorios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.components.StandardBottomBar
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.network.CategoryReportResponse
import com.example.appfinanceiro.core.network.ChartReportResponse
import com.example.appfinanceiro.core.network.SummaryResponse
import com.example.appfinanceiro.core.network.YearlySummaryResponse
import com.example.appfinanceiro.core.network.auth.RetrofitClient
import com.example.appfinanceiro.feature.home.components.MonthSelector
import com.example.appfinanceiro.feature.relatorios.components.CategoryExpensesCard
import com.example.appfinanceiro.feature.relatorios.components.IncomeVsExpenseCard
import com.example.appfinanceiro.feature.relatorios.components.YearSummarySection
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatoriosScreen(
    onNavigate: (Int) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userToken by sessionManager.token.collectAsState(initial = null)

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    var currentMonthIndex by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedRange by remember { mutableStateOf(ReportRange.ONE_MONTH) }

    var summaryData by remember { mutableStateOf<SummaryResponse?>(null) }
    var categoryData by remember { mutableStateOf<List<CategoryReportResponse>>(emptyList()) }
    var chartData by remember { mutableStateOf<List<ChartReportResponse>>(emptyList()) }
    var yearlySummary by remember { mutableStateOf<YearlySummaryResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val currentMonthNumber = currentMonthIndex + 1

    fun changeMonth(amount: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonthIndex)
            add(Calendar.MONTH, amount)
        }
        currentMonthIndex = cal.get(Calendar.MONTH)
        currentYear = cal.get(Calendar.YEAR)
    }

    LaunchedEffect(currentMonthIndex, currentYear, userToken) {
        if (userToken != null) {
            isLoading = true
            try {
                summaryData = RetrofitClient.financeApi.getSummary(
                    token = "Bearer $userToken",
                    month = currentMonthNumber,
                    year = currentYear
                )

                categoryData = RetrofitClient.financeApi.getReportCategories(
                    token = "Bearer $userToken",
                    month = currentMonthNumber,
                    year = currentYear
                )

                chartData = RetrofitClient.financeApi.getReportChart(
                    token = "Bearer $userToken",
                    year = currentYear
                )

                yearlySummary = RetrofitClient.financeApi.getYearlySummary(
                    token = "Bearer $userToken",
                    year = currentYear
                )
            } catch (e: Exception) {
                summaryData = null
                categoryData = emptyList()
                chartData = emptyList()
                yearlySummary = null
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Relatórios",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(0) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            StandardBottomBar(
                itemSelecionado = 2,
                onItemClick = onNavigate,
                onAddClick = onAddClick
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MonthSelector(
                        monthIndex = currentMonthIndex,
                        currentYear = currentYear,
                        onPrevClick = { changeMonth(-1) },
                        onNextClick = { changeMonth(1) }
                    )
                }

                item {
                    CategoryExpensesCard(
                        totalExpense = summaryData?.total_expense ?: 0.0,
                        categories = categoryData
                    )
                }

                item {
                    IncomeVsExpenseCard(
                        summaryData = summaryData,
                        chartData = chartData,
                        selectedRange = selectedRange,
                        currentMonth = currentMonthNumber,
                        onRangeSelected = { selectedRange = it }
                    )
                }

                item {
                    YearSummarySection(yearlySummary = yearlySummary)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
