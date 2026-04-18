package com.example.appfinanceiro.feature.despesas.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.theme.TextMuted

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        color = TextMuted,
        fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, top = 16.dp)
    )
}