package com.example.appfinanceiro.feature.despesas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.data.SessionManager
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextMuted
import com.example.appfinanceiro.core.network.Category
import com.example.appfinanceiro.core.network.auth.RetrofitClient
import com.example.appfinanceiro.core.network.ExpenseUpdateRequest
import com.example.appfinanceiro.feature.despesas.components.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarDespesaScreen(expenseId: Int, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userToken by remember { SessionManager(context) }.token.collectAsState(initial = null)

    val backgroundColor = MaterialTheme.colorScheme.background
    val inputBgColor = Color(0xFF1E232D)

    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }

    val sources = listOf("Salário", "Adiantamento", "Renda Extra")
    var selectedSource by remember { mutableStateOf(sources[0]) }
    var expandedSource by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'00:00:00XXX", Locale.getDefault())
    val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var dateText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var originalType by remember { mutableStateOf("Única") }

    var updateFuture by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userToken, expenseId) {
        if (userToken != null) {
            try {
                categories = RetrofitClient.financeApi.getCategories("Bearer $userToken").categories
                val expense = RetrofitClient.financeApi.getExpenseById("Bearer $userToken", expenseId)

                amountText = expense.amount.toString().replace(".", ",")
                description = expense.description
                selectedCategory = categories.find { it.id == expense.category_id }

                if (sources.contains(expense.payment_source)) {
                    selectedSource = expense.payment_source ?: sources[0]
                }

                originalType = expense.type ?: "Única"

                try {
                    val apiParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                    val parsedDate = apiParser.parse(expense.date)
                    if (parsedDate != null) {
                        calendar.time = parsedDate
                        dateText = displayFormat.format(parsedDate)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("API_DATE", "Erro ao converter a data", e)
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao carregar despesa", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            } finally {
                isLoading = false
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
                        calendar.set(selectedCal.get(Calendar.YEAR), selectedCal.get(Calendar.MONTH), selectedCal.get(Calendar.DAY_OF_MONTH))
                        dateText = displayFormat.format(calendar.time)
                    }
                    showDatePicker = false
                }) { Text("OK", color = PrimaryBlue) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = TextMuted) } }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Editar Despesa", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White) } },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryBlue) }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                ExpenseValueInput(amountText = amountText, onAmountChange = { amountText = it })
                Spacer(modifier = Modifier.height(32.dp))

                FormLabel("Descrição")
                CustomInput(description, { description = it }, Icons.Default.Description, "Ex: Supermercado", inputBgColor)

                CustomDropdown("Categoria", selectedCategory?.name ?: "Selecione", categories.map { it.name }, expandedCategory, { expandedCategory = it }) {
                    selectedCategory = categories[it]; expandedCategory = false
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomDropdown("Origem do Pagamento", selectedSource, sources, expandedSource, { expandedSource = it }) {
                    selectedSource = sources[it]; expandedSource = false
                }

                Spacer(modifier = Modifier.height(16.dp))
                FormLabel("Data de Pagamento")
                CustomInput(
                    value = dateText,
                    onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() || char == '/' }) dateText = it },
                    icon = null,
                    placeholder = "DD/MM/AAAA",
                    bgColor = inputBgColor,
                    trailingIcon = Icons.Default.CalendarToday,
                    onTrailingIconClick = { showDatePicker = true },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (originalType.equals("Parcelada", ignoreCase = true) || originalType.equals("Fixa", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().background(inputBgColor, RoundedCornerShape(12.dp)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Atualizar parcelas futuras?", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Aplica essa edição aos próximos meses", color = TextMuted, fontSize = 12.sp)
                        }
                        Switch(
                            checked = updateFuture,
                            onCheckedChange = { updateFuture = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (amountText.isEmpty() || description.isEmpty() || selectedCategory == null) {
                            Toast.makeText(context, "Preencha os campos!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val finalAmount = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                                val parsedDate = try { displayFormat.parse(dateText) ?: calendar.time } catch (e: Exception) { calendar.time }

                                val request = ExpenseUpdateRequest(
                                    amount = finalAmount,
                                    description = description,
                                    category_id = selectedCategory!!.id,
                                    payment_source = selectedSource,
                                    date = dateFormat.format(parsedDate),
                                    update_future = if (originalType.equals("Parcelada", ignoreCase = true) || originalType.equals("Fixa", ignoreCase = true)) updateFuture else null
                                )

                                RetrofitClient.financeApi.updateExpense("Bearer $userToken", expenseId, request)
                                Toast.makeText(context, "Despesa atualizada!", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                            } finally { isLoading = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Salvar Alterações", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}