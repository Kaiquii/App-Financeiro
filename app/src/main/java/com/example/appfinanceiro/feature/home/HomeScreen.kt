package com.example.appfinanceiro.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.components.StandardBottomBar
import com.example.appfinanceiro.core.network.Expense
import com.example.appfinanceiro.core.network.SummaryResponse
import com.example.appfinanceiro.core.network.auth.RetrofitClient
import com.example.appfinanceiro.feature.home.components.FilterOptionItem
import com.example.appfinanceiro.feature.home.components.MonthSelector
import com.example.appfinanceiro.feature.home.components.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (Int) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val context = LocalContext.current

    val sessionManager = remember { SessionManager(context) }
    val userToken by sessionManager.token.collectAsState(initial = null)

    var currentMonthIndex by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    var summaryData by remember { mutableStateOf<SummaryResponse?>(null) }
    var expensesData by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var categoriesMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    var showFilterModal by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    val filteredExpenses = if (selectedCategoryId == null) {
        expensesData
    } else {
        expensesData.filter { it.category_id == selectedCategoryId }
    }

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
                val catResponse = RetrofitClient.financeApi.getCategories("Bearer $userToken")
                categoriesMap = catResponse.categories.associate { it.id to it.name }

                val sumResponse = RetrofitClient.financeApi.getSummary("Bearer $userToken", currentMonthIndex + 1, currentYear)
                summaryData = sumResponse

                val expResponse = RetrofitClient.financeApi.getExpenses("Bearer $userToken", currentMonthIndex + 1, currentYear)
                expensesData = expResponse.expenses

            } catch (e: Exception) {
                android.util.Log.e("API_ERRO", "Falha ao buscar dados", e)
                summaryData = null
                expensesData = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Visão Mensal", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Voltar */ }) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = textColor) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            StandardBottomBar(
                itemSelecionado = 0,
                onItemClick = onNavigate,
                onAddClick = onAddClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
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
                ResumoFinanceiroSection(
                    isLoading = isLoading,
                    data = summaryData
                )
            }
            item {
                DespesasSection(
                    isLoading = isLoading,
                    expenses = filteredExpenses,
                    categoriesMap = categoriesMap,
                    onFilterClick = { showFilterModal = true },
                    isFiltered = selectedCategoryId != null,
                    onAddClick = onAddClick
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showFilterModal) {
        ModalBottomSheet(
            onDismissRequest = { showFilterModal = false },
            containerColor = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    "Filtrar por Categoria",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FilterOptionItem(
                    label = "Todas",
                    isSelected = selectedCategoryId == null,
                    onClick = {
                        selectedCategoryId = null
                        showFilterModal = false
                    }
                )

                categoriesMap.forEach { (id, name) ->
                    FilterOptionItem(
                        label = name,
                        isSelected = selectedCategoryId == id,
                        onClick = {
                            selectedCategoryId = id
                            showFilterModal = false
                        }
                    )
                }
            }
        }
    }
}