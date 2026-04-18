package com.example.appfinanceiro.feature.despesas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.components.StandardBottomBar
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextMuted
import com.example.appfinanceiro.core.network.Expense
import com.example.appfinanceiro.core.network.auth.RetrofitClient
import com.example.appfinanceiro.feature.home.components.MonthSelector
import com.example.appfinanceiro.feature.home.utils.getCategoryIconAndColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespesasScreen(
    onNavigate: (Int) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val userToken by sessionManager.token.collectAsState(initial = null)

    val backgroundColor = MaterialTheme.colorScheme.background
    val inputBgColor = Color(0xFF1E232D)
    val textColor = Color.White

    var expensesData by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var categoriesMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) } // 👇 Novo mapa de categorias
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }

    val calendar = remember { Calendar.getInstance() }
    var currentMonthIndex by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }

    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(currentMonthIndex, currentYear, userToken, refreshTrigger) {
        if (userToken != null) {
            isLoading = true
            try {
                val catResponse = RetrofitClient.financeApi.getCategories("Bearer $userToken")
                categoriesMap = catResponse.categories.associate { it.id to it.name }

                val expResponse = RetrofitClient.financeApi.getExpenses("Bearer $userToken", currentMonthIndex + 1, currentYear)
                expensesData = expResponse.expenses
            } catch (e: Exception) {
                expensesData = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    val filteredExpenses = expensesData.filter { expense ->
        val matchesSearch = expense.description.contains(searchQuery, ignoreCase = true)
        val matchesType = when (selectedFilter) {
            "Parceladas" -> expense.type.equals("Parcelada", ignoreCase = true)
            "Únicas" -> expense.type.equals("Única", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesType
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Despesas Mensais", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(0) }) { Icon(Icons.Default.ArrowBack, tint = textColor, contentDescription = "Voltar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            StandardBottomBar(itemSelecionado = 1, onItemClick = onNavigate, onAddClick = onAddClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar despesa...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, tint = TextMuted, contentDescription = null) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputBgColor,
                    unfocusedContainerColor = inputBgColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todas", "Parceladas", "Únicas").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) PrimaryBlue else inputBgColor, RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(filter, color = if (isSelected) Color.White else TextMuted, fontSize = 14.sp)
                    }
                }
            }

            MonthSelector(
                monthIndex = currentMonthIndex,
                currentYear = currentYear,
                onPrevClick = {
                    if (currentMonthIndex == 0) { currentMonthIndex = 11; currentYear-- } else currentMonthIndex--
                },
                onNextClick = {
                    if (currentMonthIndex == 11) { currentMonthIndex = 0; currentYear++ } else currentMonthIndex++
                }
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryBlue) }
            } else if (filteredExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Nenhuma despesa encontrada.", color = TextMuted) }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredExpenses) { expense ->
                        DespesaListItem(
                            expense = expense,
                            categoriesMap = categoriesMap,
                            onEdit = { onEditClick(expense.id) },
                            onDelete = { expenseToDelete = expense }
                        )
                    }
                }
            }
        }
    }

    if (expenseToDelete != null) {
        val isRepeating = expenseToDelete?.type?.equals("Parcelada", ignoreCase = true) == true ||
                expenseToDelete?.type?.equals("Fixa", ignoreCase = true) == true

        var deleteFutureSelected by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isDeleting) expenseToDelete = null },
            containerColor = com.example.appfinanceiro.core.designsystem.theme.BackgroundDark,
            title = {
                Text("Excluir Despesa", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Tem certeza que deseja excluir '${expenseToDelete?.description}'?", color = Color.White)

                    if (isRepeating) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { deleteFutureSelected = !deleteFutureSelected }
                        ) {
                            Checkbox(
                                checked = deleteFutureSelected,
                                onCheckedChange = { deleteFutureSelected = it },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                            )
                            Text("Excluir esta e todas as futuras", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleting = true
                        coroutineScope.launch {
                            try {
                                // 👇 Enviamos o parâmetro baseado no Checkbox
                                RetrofitClient.financeApi.deleteExpense(
                                    token = "Bearer $userToken",
                                    id = expenseToDelete!!.id,
                                    deleteFuture = if (deleteFutureSelected) true else null
                                )
                                Toast.makeText(context, "Excluído com sucesso!", Toast.LENGTH_SHORT).show()
                                expenseToDelete = null
                                refreshTrigger++
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                            } finally {
                                isDeleting = false
                            }
                        }
                    },
                    enabled = !isDeleting
                ) {
                    Text("Confirmar", color = com.example.appfinanceiro.core.designsystem.theme.DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }, enabled = !isDeleting) {
                    Text("Cancelar", color = com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun DespesaListItem(expense: Expense, categoriesMap: Map<Int, String>, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd/MM", Locale("pt", "BR")) }
    val formattedDate = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val date = parser.parse(expense.date)
        if (date != null) dateFormat.format(date) else "00/00"
    } catch (e: Exception) { "00/00" }

    val typeLabel = if (expense.type.equals("Parcelada", ignoreCase = true)) {
        "Parc. ${expense.current_installment}/${expense.installments}"
    } else {
        "Única"
    }

    val formatter = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formattedAmount = formatter.format(expense.amount)

    val categoryName = categoriesMap[expense.category_id] ?: "Outros"
    val paymentSource = expense.payment_source ?: "Salário"
    val (icon, color) = getCategoryIconAndColor(categoryName)

    val cardBg = Color(0xFF1E232D)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = cardBg
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = categoryName,
                    color = TextMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2D333E), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(typeLabel, color = TextMuted, fontSize = 11.sp)
                    }
                    Text(" • $formattedDate", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = PrimaryBlue,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onEdit() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deletar",
                        tint = Color(0xFFFF4D4D),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onDelete() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "- $formattedAmount",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(paymentSource, color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}