package com.example.appfinanceiro.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanceiro.core.designsystem.theme.PrimaryBlue

@Composable
fun StandardBottomBar(
    itemSelecionado: Int,
    onItemClick: (Int) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val bgColor = MaterialTheme.colorScheme.background
    val unselectedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    Box(contentAlignment = Alignment.BottomCenter) {
        NavigationBar(containerColor = bgColor, contentColor = unselectedColor) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                label = {
                    Text(
                        text = "Início",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp
                    )
                },
                selected = itemSelecionado == 0,
                onClick = { onItemClick(0) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Receipt, contentDescription = "Despesas") },
                label = {
                    Text(
                        text = "Despesas",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp
                    )
                },
                selected = itemSelecionado == 1,
                onClick = { onItemClick(1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )

            NavigationBarItem(
                icon = { },
                label = { },
                selected = false,
                onClick = { },
                enabled = false
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.PieChart, contentDescription = "Relatório") },
                label = {
                    Text(
                        text = "Relatórios",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp
                    )
                },
                selected = itemSelecionado == 2,
                onClick = { onItemClick(2) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                label = {
                    Text(
                        text = "Perfil",
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp
                    )
                },
                selected = itemSelecionado == 3,
                onClick = { onItemClick(3) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }

        Box(
            modifier = Modifier
                .offset(y = (-40).dp)
                .size(68.dp)
                .background(bgColor, CircleShape)
                .padding(6.dp)
        ) {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = PrimaryBlue,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
            }
        }
    }
}
