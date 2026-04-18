package com.example.appfinanceiro.feature.despesas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import com.example.appfinanceiro.core.designsystem.theme.TextMuted

@Composable
fun ExpenseValueInput(amountText: String, onAmountChange: (String) -> Unit) {
    Text("Valor da Despesa", color = TextMuted, fontSize = 14.sp)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Text("R$", color = TextMuted, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = amountText,
            onValueChange = { newValue ->
                val formatado = newValue.replace(".", ",")
                if (formatado.count { it == ',' } <= 1 && formatado.all { it.isDigit() || it == ',' }) {
                    onAmountChange(formatado)
                }
            },
            textStyle = TextStyle(color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(PrimaryBlue),
            modifier = Modifier.width(IntrinsicSize.Min),
            decorationBox = { innerTextField ->
                if (amountText.isEmpty()) Text("0,00", color = TextMuted, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                innerTextField()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (Int) -> Unit
) {
    val inputBgColor = Color(0xFF1E232D)
    val dropdownBgColor = Color(0xFF2A2F3D)

    FormLabel(label)
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        CustomInput(
            value = selectedValue,
            onValueChange = {},
            icon = null,
            placeholder = "",
            bgColor = inputBgColor,
            readOnly = true,
            trailingIcon = Icons.Default.ArrowDropDown,
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(dropdownBgColor)
        ) {
            options.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text, color = Color.White) },
                    onClick = { onSelect(index) }
                )
            }
        }
    }
}

@Composable
fun PaymentTypeSelector(
    selectedType: String,
    onTypeChange: (String) -> Unit
) {
    val inputBgColor = Color(0xFF1E232D)
    val options = listOf("Única", "Parcelada", "Fixa")

    FormLabel("Tipo de Pagamento")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(inputBgColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        options.forEach { option ->
            val isSelected = selectedType.equals(option, ignoreCase = true)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else Color.Transparent
                    )
                    .clickable { onTypeChange(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) PrimaryBlue else TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun InstallmentCounter(installments: Int, onInstallmentChange: (Int) -> Unit) {
    val inputBgColor = Color(0xFF1E232D)

    Spacer(modifier = Modifier.height(16.dp))
    FormLabel("Número de Parcelas")
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp).background(inputBgColor, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("-", color = Color.White, fontSize = 24.sp, modifier = Modifier.clickable { if (installments > 1) onInstallmentChange(installments - 1) }.padding(8.dp))
        Text("${installments}x", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("+", color = Color.White, fontSize = 24.sp, modifier = Modifier.clickable { onInstallmentChange(installments + 1) }.padding(8.dp))
    }
}