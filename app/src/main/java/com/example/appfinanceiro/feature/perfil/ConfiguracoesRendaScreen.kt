package com.example.appfinanceiro.feature.perfil

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextMuted
import com.example.appfinanceiro.core.network.Category
import com.example.appfinanceiro.core.network.CategoryRequest
import com.example.appfinanceiro.core.network.Income
import com.example.appfinanceiro.core.network.IncomeRequest
import com.example.appfinanceiro.core.network.IncomeUpdateRequest
import com.example.appfinanceiro.core.network.auth.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesRendaScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userToken by remember { SessionManager(context) }.token.collectAsState(initial = null)

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    val calendar = remember { Calendar.getInstance() }
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentYear = calendar.get(Calendar.YEAR)

    var incomes by remember { mutableStateOf<List<Income>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    var salarioAmount by remember { mutableStateOf("") }
    var adiantamentoAmount by remember { mutableStateOf("") }
    var rendaExtraAmount by remember { mutableStateOf("") }

    var salarioUpdateFuture by remember { mutableStateOf(true) }
    var adiantamentoUpdateFuture by remember { mutableStateOf(true) }
    var rendaExtraUpdateFuture by remember { mutableStateOf(true) }

    var incomeToDelete by remember { mutableStateOf<Income?>(null) }
    var incomeDeleteLabel by remember { mutableStateOf("") }

    LaunchedEffect(userToken, refreshTrigger) {
        if (userToken != null) {
            isLoading = true
            try {
                val incomesResponse = RetrofitClient.financeApi.getIncomes("Bearer $userToken")

                incomes = incomesResponse.incomes

                salarioAmount = currentIncome(incomes, "Salario", currentMonth, currentYear)?.amount?.toString()?.replace(".", ",") ?: ""
                adiantamentoAmount = currentIncome(incomes, "Adiantamento", currentMonth, currentYear)?.amount?.toString()?.replace(".", ",") ?: ""
                rendaExtraAmount = currentIncome(incomes, "Renda Extra", currentMonth, currentYear)?.amount?.toString()?.replace(".", ",") ?: ""
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao carregar configurações", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    val salarioAtual = currentIncome(incomes, "Salario", currentMonth, currentYear)
    val adiantamentoAtual = currentIncome(incomes, "Adiantamento", currentMonth, currentYear)
    val rendaExtraAtual = currentIncome(incomes, "Renda Extra", currentMonth, currentYear)

    if (incomeToDelete != null) {
        AlertDialog(
            onDismissRequest = { incomeToDelete = null },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Excluir renda",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Tem certeza que deseja remover $incomeDeleteLabel?",
                    color = textColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedIncome = incomeToDelete ?: return@TextButton

                        coroutineScope.launch {
                            try {
                                RetrofitClient.financeApi.deleteIncome(
                                    "Bearer $userToken",
                                    selectedIncome.id
                                )
                                Toast.makeText(context, "$incomeDeleteLabel removido!", Toast.LENGTH_SHORT).show()
                                incomeToDelete = null
                                refreshTrigger++
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao remover $incomeDeleteLabel", Toast.LENGTH_SHORT).show()
                                incomeToDelete = null
                            }
                        }
                    }
                ) {
                    Text("Sim", color = Color(0xFFFF7A7A), fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { incomeToDelete = null }) {
                    Text("Cancelar", color = PrimaryBlue, fontWeight = FontWeight.Medium)
                }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Configurações de Renda", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text("Rendas Fixas", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Referência atual: ${currentMonth.toString().padStart(2, '0')}/$currentYear",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }

                item {
                    IncomeCard(
                        title = "Salário",
                        amount = salarioAmount,
                        onAmountChange = { salarioAmount = it },
                        existingIncome = salarioAtual,
                        updateFuture = salarioUpdateFuture,
                        onUpdateFutureChange = { salarioUpdateFuture = it },
                        onSave = {
                            saveIncome(
                                context = context,
                                source = "Salario",
                                amountText = salarioAmount,
                                existingIncome = salarioAtual,
                                updateFuture = salarioUpdateFuture,
                                month = currentMonth,
                                year = currentYear,
                                token = userToken,
                                refresh = { refreshTrigger++ }
                            )
                        },
                        onDelete = {
                            salarioAtual?.let {
                                incomeToDelete = it
                                incomeDeleteLabel = "o salário"
                            }
                        },
                        surfaceColor = surfaceColor
                    )
                }

                item {
                    IncomeCard(
                        title = "Adiantamento",
                        amount = adiantamentoAmount,
                        onAmountChange = { adiantamentoAmount = it },
                        existingIncome = adiantamentoAtual,
                        updateFuture = adiantamentoUpdateFuture,
                        onUpdateFutureChange = { adiantamentoUpdateFuture = it },
                        onSave = {
                            saveIncome(
                                context = context,
                                source = "Adiantamento",
                                amountText = adiantamentoAmount,
                                existingIncome = adiantamentoAtual,
                                updateFuture = adiantamentoUpdateFuture,
                                month = currentMonth,
                                year = currentYear,
                                token = userToken,
                                refresh = { refreshTrigger++ }
                            )
                        },
                        onDelete = {
                            adiantamentoAtual?.let {
                                incomeToDelete = it
                                incomeDeleteLabel = "o adiantamento"
                            }
                        },
                        surfaceColor = surfaceColor
                    )
                }

                item {
                    IncomeCard(
                        title = "Renda Extra",
                        amount = rendaExtraAmount,
                        onAmountChange = { rendaExtraAmount = it },
                        existingIncome = rendaExtraAtual,
                        updateFuture = rendaExtraUpdateFuture,
                        onUpdateFutureChange = { rendaExtraUpdateFuture = it },
                        onSave = {
                            saveIncome(
                                context = context,
                                source = "Renda Extra",
                                amountText = rendaExtraAmount,
                                existingIncome = rendaExtraAtual,
                                updateFuture = rendaExtraUpdateFuture,
                                month = currentMonth,
                                year = currentYear,
                                token = userToken,
                                refresh = { refreshTrigger++ }
                            )
                        },
                        onDelete = {
                            rendaExtraAtual?.let {
                                incomeToDelete = it
                                incomeDeleteLabel = "a renda extra"
                            }
                        },
                        surfaceColor = surfaceColor
                    )
                }
            }
        }
    }
}

private fun currentIncome(incomes: List<Income>, source: String, month: Int, year: Int): Income? {
    return incomes.firstOrNull {
        it.source.equals(source, ignoreCase = true) &&
                it.month == month &&
                it.year == year
    }
}

private fun saveIncome(
    context: android.content.Context,
    source: String,
    amountText: String,
    existingIncome: Income?,
    updateFuture: Boolean,
    month: Int,
    year: Int,
    token: String?,
    refresh: () -> Unit
) {
    val amount = amountText.replace(",", ".").toDoubleOrNull()
    if (amount == null) {
        Toast.makeText(context, "Informe um valor válido", Toast.LENGTH_SHORT).show()
        return
    }

    val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
    scope.launch {
        try {
            if (existingIncome == null) {
                RetrofitClient.financeApi.createIncome(
                    "Bearer $token",
                    IncomeRequest(
                        source = source,
                        amount = amount,
                        month = month,
                        year = year,
                        type = "Fixa"
                    )
                )
                Toast.makeText(context, "$source cadastrado!", Toast.LENGTH_SHORT).show()
            } else {
                RetrofitClient.financeApi.updateIncome(
                    "Bearer $token",
                    existingIncome.id,
                    IncomeUpdateRequest(
                        amount = amount,
                        update_future = updateFuture
                    )
                )
                Toast.makeText(context, "$source atualizado!", Toast.LENGTH_SHORT).show()
            }
            refresh()
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao salvar $source", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun IncomeCard(
    title: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    existingIncome: Income?,
    updateFuture: Boolean,
    onUpdateFutureChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    surfaceColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        OutlinedTextField(
            value = amount,
            onValueChange = {
                if (it.count { c -> c == ',' } <= 1 && it.all { c -> c.isDigit() || c == ',' }) {
                    onAmountChange(it)
                }
            },
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (existingIncome != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = updateFuture,
                    onCheckedChange = onUpdateFutureChange
                )
                Text("Atualizar este e os próximos meses", fontSize = 13.sp, color = TextMuted)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(if (existingIncome == null) "Cadastrar" else "Atualizar")
            }

            if (existingIncome != null) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Excluir")
                }
            }
        }
    }
}
