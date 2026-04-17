package com.example.appfinanceiro.core.network.home.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appfinanceiro.core.designsystem.theme.DangerRed
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(value)
}

fun formatExpenseDate(dateString: String): String {
    return try {
        val parts = dateString.split("T")[0].split("-")
        if (parts.size == 3) "${parts[2]}/${parts[1]}" else dateString
    } catch (e: Exception) {
        dateString
    }
}

fun getCategoryIconAndColor(categoryName: String): Pair<ImageVector, Color> {
    return when (categoryName.lowercase()) {
        "aluguel", "moradia" -> Pair(Icons.Default.Home, DangerRed)
        "supermercado", "alimentação" -> Pair(Icons.Default.ShoppingCart, PrimaryBlue)
        "celular", "eletrônicos" -> Pair(Icons.Default.PhoneIphone, Color(0xFF9C27B0))
        "energia elétrica", "luz", "contas" -> Pair(Icons.Default.FlashOn, Color(0xFFFF9800))
        else -> Pair(Icons.Default.Receipt, PrimaryBlue)
    }
}