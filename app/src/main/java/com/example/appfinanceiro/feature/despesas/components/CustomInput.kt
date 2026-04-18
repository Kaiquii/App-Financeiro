package com.example.appfinanceiro.feature.despesas.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.appfinanceiro.core.designsystem.theme.TextMuted

@Composable
fun CustomInput(
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector?,
    placeholder: String,
    bgColor: Color,
    readOnly: Boolean = false,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    val leading: (@Composable () -> Unit)? = if (icon != null) {
        { Icon(icon, contentDescription = null, tint = TextMuted) }
    } else null

    val trailing: (@Composable () -> Unit)? = if (trailingIcon != null) {
        {
            if (onTrailingIconClick != null) {
                IconButton(onClick = onTrailingIconClick) {
                    Icon(trailingIcon, contentDescription = null, tint = TextMuted)
                }
            } else {
                Icon(trailingIcon, contentDescription = null, tint = TextMuted)
            }
        }
    } else null

    TextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        placeholder = { Text(placeholder, color = TextMuted) },
        leadingIcon = leading,
        trailingIcon = trailing,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = bgColor,
            unfocusedContainerColor = bgColor,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions
    )
}