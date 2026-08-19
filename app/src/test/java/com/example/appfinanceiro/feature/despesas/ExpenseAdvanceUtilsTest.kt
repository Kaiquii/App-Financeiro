package com.example.appfinanceiro.feature.despesas

import com.example.appfinanceiro.core.network.Expense
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseAdvanceUtilsTest {

    @Test
    fun returnsOnlyAdvancedExpensesScheduledOutsideSelectedMonth() {
        val incoming = incomingAdvancedExpenses(
            effectiveExpenses = listOf(
                expense(id = 1, date = "2026-09-10T00:00:00-03:00", isAdvanced = true),
                expense(id = 2, date = "2026-08-10T00:00:00-03:00", isAdvanced = true),
                expense(id = 3, date = "2026-10-10T00:00:00-03:00", isAdvanced = false)
            ),
            selectedMonth = 8,
            selectedYear = 2026
        )

        assertEquals(listOf(1), incoming.map { it.id })
    }

    @Test
    fun returnsEmptyListWhenThereAreNoIncomingAdvances() {
        val incoming = incomingAdvancedExpenses(
            effectiveExpenses = listOf(
                expense(id = 1, date = "2026-08-10", isAdvanced = true),
                expense(id = 2, date = "2026-09-10", isAdvanced = false)
            ),
            selectedMonth = 8,
            selectedYear = 2026
        )

        assertTrue(incoming.isEmpty())
    }

    @Test
    fun removesDuplicateRecordsUsingExpenseId() {
        val duplicated = expense(
            id = 15,
            date = "2026-09-10T00:00:00-03:00",
            isAdvanced = true
        )

        val incoming = incomingAdvancedExpenses(
            effectiveExpenses = listOf(duplicated, duplicated.copy(description = "Mesmo registro")),
            selectedMonth = 8,
            selectedYear = 2026
        )

        assertEquals(1, incoming.size)
        assertEquals(15, incoming.single().id)
    }

    private fun expense(id: Int, date: String, isAdvanced: Boolean) = Expense(
        id = id,
        category_id = 1,
        amount = 70.0,
        description = "X Burguer",
        date = date,
        type = "Única",
        installments = null,
        current_installment = null,
        payment_source = "Salário",
        isAdvanced = isAdvanced,
        advancedAt = if (isAdvanced) "2026-08-19T00:00:00-03:00" else null
    )
}
