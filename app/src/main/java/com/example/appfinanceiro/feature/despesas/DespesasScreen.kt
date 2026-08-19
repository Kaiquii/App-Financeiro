package com.example.appfinanceiro.feature.despesas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.components.AppDataErrorBanner
import com.example.appfinanceiro.core.designsystem.components.AppLoadingIndicator
import com.example.appfinanceiro.core.designsystem.components.ExpenseDetailsDialog
import com.example.appfinanceiro.core.designsystem.components.ExpenseCard
import com.example.appfinanceiro.core.designsystem.components.ExpenseCardStyle
import com.example.appfinanceiro.core.designsystem.components.StandardBottomBar
import com.example.appfinanceiro.core.designsystem.components.swipeNavigation
import com.example.appfinanceiro.core.designsystem.components.dataRequestErrorMessage
import com.example.appfinanceiro.core.designsystem.theme.DangerRed
import com.example.appfinanceiro.core.designsystem.theme.GreenPositive
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextMuted
import com.example.appfinanceiro.core.network.Expense
import com.example.appfinanceiro.core.network.paymentCardSourceLabel
import com.example.appfinanceiro.feature.home.components.MonthSelector
import com.example.appfinanceiro.feature.home.utils.getCategoryIconAndColor
import com.example.appfinanceiro.feature.despesas.components.AdvanceExpenseDialog
import com.example.appfinanceiro.feature.despesas.components.RemoveAdvanceDialog
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespesasScreen(
    onNavigate: (Int) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onSessionExpired: () -> Unit = {},
    viewModel: DespesasViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userToken by sessionManager.token.collectAsState(initial = null)
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val colorScheme = MaterialTheme.colorScheme

    val backgroundColor = colorScheme.background
    val inputBgColor = colorScheme.surface
    val textColor = colorScheme.onBackground
    val surfaceTextColor = colorScheme.onSurface
    val secondaryTextColor = colorScheme.onSurfaceVariant

    var refreshTrigger by remember { mutableIntStateOf(0) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("Todas") }
    var selectedPaymentStatus by rememberSaveable { mutableStateOf<String?>(null) }
    var showPaymentStatusFilterModal by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    var currentMonthIndex by rememberSaveable {
        mutableIntStateOf(calendar.get(Calendar.MONTH))
    }
    var currentYear by rememberSaveable {
        mutableIntStateOf(calendar.get(Calendar.YEAR))
    }

    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    var expenseToView by remember { mutableStateOf<Expense?>(null) }
    var expensePaymentStatusToChange by remember { mutableStateOf<Expense?>(null) }
    var expenseToAdvance by remember { mutableStateOf<Expense?>(null) }
    var expenseToRemoveAdvance by remember { mutableStateOf<Expense?>(null) }

    LaunchedEffect(currentMonthIndex, currentYear, userToken, refreshTrigger, selectedPaymentStatus) {
        userToken?.let { token ->
            viewModel.loadExpenses(
                token,
                currentMonthIndex + 1,
                currentYear,
                selectedPaymentStatus
            )
        }
    }

    LaunchedEffect(uiState.isSessionExpired) {
        if (uiState.isSessionExpired) {
            sessionManager.clearSession()
            viewModel.clearSessionExpired()
            onSessionExpired()
        }
    }

    LaunchedEffect(
        uiState.deleteSuccessMessage,
        uiState.deleteErrorMessage,
        uiState.paymentStatusSuccessMessage,
        uiState.paymentStatusErrorMessage,
        uiState.advanceStatusSuccessMessage,
        uiState.advanceStatusErrorMessage
    ) {
        uiState.deleteSuccessMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }

        uiState.deleteErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }

        uiState.paymentStatusSuccessMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }

        uiState.paymentStatusErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.advanceStatusSuccessMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.advanceStatusErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    val expenseFilters = listOf("Todas", "Parceladas", "Únicas", "Fixas")
    val expenseCountsByFilter = expenseCountsByFilter(
        expenses = uiState.expensesData,
        searchQuery = searchQuery,
        filters = expenseFilters
    )
    val filteredExpenses = filterExpenses(
        expenses = uiState.expensesData,
        searchQuery = searchQuery,
        selectedFilter = selectedFilter
    )
    val incomingAdvanced = incomingAdvancedExpenses(
        effectiveExpenses = uiState.effectiveExpensesData,
        selectedMonth = currentMonthIndex + 1,
        selectedYear = currentYear
    )
    val filteredIncomingAdvanced = filterExpenses(
        expenses = incomingAdvanced,
        searchQuery = searchQuery,
        selectedFilter = selectedFilter
    )
    val selectedFilterCount = expenseCountsByFilter[selectedFilter] ?: filteredExpenses.size
    val selectedFilterTotal = totalExpenseAmount(filteredExpenses)
    val selectedFilterTotalLabel = when (selectedFilter) {
        "Parceladas" -> "Total em Parceladas"
        "Únicas" -> "Total em Únicas"
        "Fixas" -> "Total em Fixas"
        else -> null
    }
    val formattedSelectedFilterTotal = remember(selectedFilterTotal) {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
            .format(selectedFilterTotal)
    }
    val expensesErrorBannerMessage = uiState.errorMessage?.let { error ->
        dataRequestErrorMessage(
            errorMessage = error,
            showingPreviousData = uiState.expensesData.isNotEmpty(),
            dataLabel = "as despesas"
        )
    }

    Scaffold(
        modifier = Modifier.swipeNavigation(1, onNavigate),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Despesas Mensais",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(0) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            tint = textColor,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (uiState.isLoading && uiState.hasLoadedOnce) {
                        AppLoadingIndicator(modifier = Modifier.padding(end = 16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            StandardBottomBar(
                itemSelecionado = 1,
                onItemClick = onNavigate,
                onAddClick = onAddClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text("Buscar despesa...", color = secondaryTextColor)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        tint = secondaryTextColor,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showPaymentStatusFilterModal = true }) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Filtrar status de pagamento: ${
                                when (selectedPaymentStatus) {
                                    "paid" -> "Pagas"
                                    "pending" -> "Pendentes"
                                    else -> "Todas"
                                }
                            }",
                            tint = if (selectedPaymentStatus == null) {
                                secondaryTextColor
                            } else if (selectedPaymentStatus == "paid") {
                                GreenPositive
                            } else {
                                PrimaryBlue
                            }
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputBgColor,
                    unfocusedContainerColor = inputBgColor,
                    disabledContainerColor = inputBgColor,
                    focusedTextColor = surfaceTextColor,
                    unfocusedTextColor = surfaceTextColor,
                    cursorColor = PrimaryBlue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedPlaceholderColor = secondaryTextColor,
                    unfocusedPlaceholderColor = secondaryTextColor,
                    focusedLeadingIconColor = secondaryTextColor,
                    unfocusedLeadingIconColor = secondaryTextColor
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            val filterChipBg = TextMuted.copy(alpha = 0.2f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                expenseFilters.forEach { filter ->
                    val isSelected = selectedFilter == filter

                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) PrimaryBlue else filterChipBg,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else TextMuted,
                            fontSize = 14.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }

            MonthSelector(
                monthIndex = currentMonthIndex,
                currentYear = currentYear,
                onPrevClick = {
                    if (currentMonthIndex == 0) {
                        currentMonthIndex = 11
                        currentYear--
                    } else {
                        currentMonthIndex--
                    }
                },
                onNextClick = {
                    if (currentMonthIndex == 11) {
                        currentMonthIndex = 0
                        currentYear++
                    } else {
                        currentMonthIndex++
                    }
                },
                centerSuffix = when {
                    uiState.errorMessage != null && uiState.expensesData.isEmpty() -> null
                    selectedFilterCount == 1 -> "1 despesa"
                    else -> "$selectedFilterCount despesas"
                }
            )

            if (
                selectedFilterTotalLabel != null &&
                (uiState.hasLoadedOnce || uiState.expensesData.isNotEmpty()) &&
                !(uiState.errorMessage != null && uiState.expensesData.isEmpty())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedFilterTotalLabel,
                        color = secondaryTextColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = formattedSelectedFilterTotal,
                        color = PrimaryBlue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            expensesErrorBannerMessage?.let { message ->
                AppDataErrorBanner(
                    message = message,
                    isRetrying = uiState.isLoading,
                    onRetry = {
                        userToken?.let { token ->
                            viewModel.loadExpenses(
                                token,
                                currentMonthIndex + 1,
                                currentYear,
                                selectedPaymentStatus
                            )
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (uiState.isLoading && !uiState.hasLoadedOnce) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AppLoadingIndicator(size = 40.dp, strokeWidth = 4.dp)
                }
            } else if (uiState.errorMessage != null && uiState.expensesData.isEmpty()) {
                Spacer(modifier = Modifier.fillMaxSize())
            } else if (filteredExpenses.isEmpty() && filteredIncomingAdvanced.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma despesa encontrada.", color = secondaryTextColor)
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (filteredExpenses.isNotEmpty()) {
                        item(key = "scheduled_header") {
                            Text(
                                text = "Despesas previstas para ${monthName(currentMonthIndex + 1)}",
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    items(
                        items = filteredExpenses,
                        key = { expense -> "scheduled_${expense.id}" }
                    ) { expense ->
                        DespesaListItem(
                            expense = expense,
                            categoriesMap = uiState.categoriesMap,
                            onView = { expenseToView = expense },
                            onEdit = { onEditClick(expense.id) },
                            onDelete = { expenseToDelete = expense },
                            onPaymentStatusClick = { expensePaymentStatusToChange = expense }
                        )
                    }

                    if (filteredIncomingAdvanced.isNotEmpty()) {
                        item(key = "incoming_advanced_header") {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Adiantadas de outros meses",
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Incluídas no total de ${monthName(currentMonthIndex + 1)}",
                                    color = secondaryTextColor,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        items(
                            items = filteredIncomingAdvanced,
                            key = { expense -> "incoming_${expense.id}" }
                        ) { expense ->
                            DespesaListItem(
                                expense = expense,
                                categoriesMap = uiState.categoriesMap,
                                onView = { expenseToView = expense },
                                onEdit = { onEditClick(expense.id) },
                                onDelete = { expenseToDelete = expense },
                                onPaymentStatusClick = { expensePaymentStatusToChange = expense }
                            )
                        }
                    }
                }
            }
        }
    }

    if (expenseToDelete != null) {
        val isInstallmentExpense =
            expenseToDelete?.type?.equals("Parcelada", ignoreCase = true) == true
        val isFixedExpense =
            expenseToDelete?.type?.equals("Fixa", ignoreCase = true) == true

        var deleteFutureSelected by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!uiState.isDeleting) expenseToDelete = null },
            containerColor = backgroundColor,
            titleContentColor = textColor,
            textContentColor = textColor,
            title = {
                Text(
                    "Excluir Despesa",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Tem certeza que deseja excluir '${expenseToDelete?.description}'?",
                        color = textColor
                    )

                    if (isFixedExpense) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Esta exclusão removerá esta despesa no mês atual e também nos próximos meses.",
                            color = secondaryTextColor,
                            fontSize = 14.sp
                        )
                    } else if (isInstallmentExpense) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                deleteFutureSelected = !deleteFutureSelected
                            }
                        ) {
                            Checkbox(
                                checked = deleteFutureSelected,
                                onCheckedChange = { deleteFutureSelected = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryBlue,
                                    uncheckedColor = secondaryTextColor,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                "Excluir esta e todas as futuras",
                                color = textColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val token = userToken ?: return@TextButton
                        val selectedExpense = expenseToDelete ?: return@TextButton

                        viewModel.deleteExpense(
                            token = token,
                            expenseId = selectedExpense.id,
                            deleteFuture = if (isInstallmentExpense && deleteFutureSelected) true else null,
                            onDeleted = {
                                expenseToDelete = null
                                refreshTrigger++
                            }
                        )
                    },
                    enabled = !uiState.isDeleting
                ) {
                    Text(
                        "Confirmar",
                        color = DangerRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { expenseToDelete = null },
                    enabled = !uiState.isDeleting
                ) {
                    Text("Cancelar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    expensePaymentStatusToChange?.let { expense ->
        val markingAsPaid = !expense.is_paid
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isUpdatingPaymentStatus) expensePaymentStatusToChange = null
            },
            containerColor = backgroundColor,
            title = {
                Text(
                    text = if (markingAsPaid) "Marcar como paga?" else "Desmarcar como paga?",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (markingAsPaid) {
                        "Confirma que \"${expense.description}\" foi paga?"
                    } else {
                        "Confirma que deseja desmarcar \"${expense.description}\" como paga?"
                    },
                    color = secondaryTextColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val token = userToken ?: return@TextButton
                        viewModel.updateExpensePaymentStatus(
                            token = token,
                            expense = expense,
                            isPaid = markingAsPaid,
                            onUpdated = { expensePaymentStatusToChange = null }
                        )
                    },
                    enabled = !uiState.isUpdatingPaymentStatus
                ) {
                    Text(
                        text = if (uiState.isUpdatingPaymentStatus) {
                            "Atualizando..."
                        } else if (markingAsPaid) {
                            "Marcar como paga"
                        } else {
                            "Desmarcar"
                        },
                        color = if (markingAsPaid) GreenPositive else PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { expensePaymentStatusToChange = null },
                    enabled = !uiState.isUpdatingPaymentStatus
                ) {
                    Text("Cancelar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showPaymentStatusFilterModal) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentStatusFilterModal = false },
            containerColor = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    text = "Filtrar por Status",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PaymentStatusFilterOption(
                    label = "Todas",
                    isSelected = selectedPaymentStatus == null,
                    onClick = {
                        selectedPaymentStatus = null
                        showPaymentStatusFilterModal = false
                    }
                )
                PaymentStatusFilterOption(
                    label = "Pendentes",
                    isSelected = selectedPaymentStatus == "pending",
                    onClick = {
                        selectedPaymentStatus = "pending"
                        showPaymentStatusFilterModal = false
                    }
                )
                PaymentStatusFilterOption(
                    label = "Pagas",
                    isSelected = selectedPaymentStatus == "paid",
                    onClick = {
                        selectedPaymentStatus = "paid"
                        showPaymentStatusFilterModal = false
                    }
                )
            }
        }
    }

    expenseToView?.let { expense ->
        ExpenseDetailsDialog(
            expense = expense,
            categoryName = uiState.categoriesMap[expense.category_id] ?: "Outros",
            onDismiss = { expenseToView = null },
            onAdvanceClick = {
                expenseToView = null
                expenseToAdvance = expense
            },
            onChangeAdvanceDateClick = if (expense.isAdvanced) {
                {
                    expenseToView = null
                    expenseToAdvance = expense
                }
            } else {
                null
            },
            onRemoveAdvanceClick = if (expense.isAdvanced) {
                {
                    expenseToView = null
                    expenseToRemoveAdvance = expense
                }
            } else {
                null
            }
        )
    }

    expenseToAdvance?.let { expense ->
        AdvanceExpenseDialog(
            expense = expense,
            isUpdating = uiState.isUpdatingAdvanceStatus,
            onDismiss = { expenseToAdvance = null },
            onConfirm = { date ->
                val token = userToken ?: return@AdvanceExpenseDialog
                viewModel.updateAdvanceStatus(
                    token = token,
                    expense = expense,
                    isAdvanced = true,
                    advancedAt = date,
                    onUpdated = {
                        expenseToAdvance = null
                        refreshTrigger++
                    }
                )
            }
        )
    }

    expenseToRemoveAdvance?.let { expense ->
        RemoveAdvanceDialog(
            expense = expense,
            isUpdating = uiState.isUpdatingAdvanceStatus,
            onDismiss = { expenseToRemoveAdvance = null },
            onConfirm = {
                val token = userToken ?: return@RemoveAdvanceDialog
                viewModel.updateAdvanceStatus(
                    token = token,
                    expense = expense,
                    isAdvanced = false,
                    advancedAt = null,
                    onUpdated = {
                        expenseToRemoveAdvance = null
                        refreshTrigger++
                    }
                )
            }
        )
    }
}

@Composable
private fun PaymentStatusFilterOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) PrimaryBlue.copy(alpha = 0.16f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun DespesaListItem(
    expense: Expense,
    categoriesMap: Map<Int, String>,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPaymentStatusClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM", Locale("pt", "BR")) }
    val formattedDate = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val date = parser.parse(expense.date)
        if (date != null) dateFormat.format(date) else "00/00"
    } catch (e: Exception) {
        "00/00"
    }

    val typeLabel = when {
        expense.type.equals("Parcelada", ignoreCase = true) ->
            "Parc. ${expense.current_installment}/${expense.installments}"

        expense.type.equals("Fixa", ignoreCase = true) -> "Fixa"
        else -> "Única"
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formattedAmount = formatter.format(expense.amount)

    val categoryName = categoriesMap[expense.category_id] ?: "Outros"
    val paymentSource = if (expense.payment_splits.size > 1) {
        "Pag. dividido"
    } else {
        expense.paymentCardSourceLabel()
    }
    val (icon, color) = getCategoryIconAndColor(categoryName)

    ExpenseCard(
        style = ExpenseCardStyle.Detailed,
        icon = icon,
        iconColor = color,
        title = expense.description,
        categoryName = categoryName,
        paymentSource = paymentSource,
        type = typeLabel,
        date = formattedDate,
        value = "- $formattedAmount",
        notes = expense.notes,
        isPaid = expense.is_paid,
        isAdvanced = expense.isAdvanced,
        advancedLabel = formatAdvancedDate(expense.advancedAt)?.let { "Adiantada em $it" },
        showDate = !expense.isAdvanced,
        onView = onView,
        onEdit = onEdit,
        onDelete = onDelete,
        onPaymentStatusClick = onPaymentStatusClick
    )
}
