package com.example.appfinanceiro.feature.despesas

import com.example.appfinanceiro.feature.despesas.components.PaymentSplitInput
import com.example.appfinanceiro.feature.despesas.components.paymentSplitValidationMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExpenseFormValidatorTest {
    @Test
    fun requiresAmountDescriptionAndCategory() {
        val result = validateExpenseForm("", "", null, "", "Campos obrigatórios")
        assertEquals("Campos obrigatórios", result)
    }

    @Test
    fun limitsNotesToFiveHundredCharacters() {
        val result = validateExpenseForm("10", "Compra", 1, "a".repeat(501), "Campos")
        assertEquals("Observações devem ter no máximo 500 caracteres.", result)
    }

    @Test
    fun acceptsTheCurrentValidFormRules() {
        val result = validateExpenseForm("10,50", "Compra", 1, "Observação", "Campos")
        assertNull(result)
    }

    @Test
    fun acceptsPaymentSplitsThatMatchTheExpenseAmount() {
        val result = paymentSplitValidationMessage(
            amount = 1200.0,
            splits = listOf(
                PaymentSplitInput("Salário", "1000,00"),
                PaymentSplitInput("Renda Extra", "200,00")
            )
        )

        assertNull(result)
    }

    @Test
    fun rejectsRepeatedOrIncompletePaymentSplits() {
        val repeated = paymentSplitValidationMessage(
            amount = 1200.0,
            splits = listOf(
                PaymentSplitInput("Salário", "600"),
                PaymentSplitInput("Salário", "600")
            )
        )
        val incomplete = paymentSplitValidationMessage(
            amount = 1200.0,
            splits = listOf(PaymentSplitInput("Salário", "1000"))
        )

        assertEquals("Não repita uma origem de pagamento.", repeated)
        assertEquals("Distribua todo o valor da despesa entre as origens.", incomplete)
    }
}
