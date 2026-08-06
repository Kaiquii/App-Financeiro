package com.example.appfinanceiro.feature.despesas

import com.example.appfinanceiro.MainDispatcherRule
import com.example.appfinanceiro.core.data.ExpensesDataSource
import com.example.appfinanceiro.core.network.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DespesasViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadsExpensesAndCategoryNames() = runTest(mainDispatcherRule.testDispatcher) {
        val dataSource = FakeExpensesDataSource()
        val viewModel = DespesasViewModel(dataSource)

        viewModel.loadExpenses("token", 7, 2026)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.expensesData.size)
        assertEquals("Casa", viewModel.uiState.value.categoriesMap[1])
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun deletesExpenseAndNotifiesTheScreen() = runTest(mainDispatcherRule.testDispatcher) {
        val dataSource = FakeExpensesDataSource()
        val viewModel = DespesasViewModel(dataSource)
        var callbackCalled = false

        viewModel.deleteExpense("token", 7, true) { callbackCalled = true }
        advanceUntilIdle()

        assertEquals(7, dataSource.deletedId)
        assertEquals(true, dataSource.deletedFuture)
        assertTrue(callbackCalled)
        assertEquals("Excluído com sucesso!", viewModel.uiState.value.deleteSuccessMessage)
        assertFalse(viewModel.uiState.value.isDeleting)
    }

    @Test
    fun updatesOnlyTheSelectedExpensePaymentStatus() = runTest(mainDispatcherRule.testDispatcher) {
        val dataSource = FakeExpensesDataSource()
        val viewModel = DespesasViewModel(dataSource)
        var callbackCalled = false

        viewModel.loadExpenses("token", 7, 2026)
        advanceUntilIdle()
        val expense = viewModel.uiState.value.expensesData.single()

        viewModel.updateExpensePaymentStatus("token", expense, true) { callbackCalled = true }
        advanceUntilIdle()

        assertEquals(7, dataSource.paymentStatusUpdatedId)
        assertEquals(true, dataSource.paymentStatusUpdatedToPaid)
        assertTrue(viewModel.uiState.value.expensesData.single().is_paid)
        assertTrue(callbackCalled)
        assertFalse(viewModel.uiState.value.isUpdatingPaymentStatus)
    }

    private class FakeExpensesDataSource : ExpensesDataSource {
        var deletedId: Int? = null
        var deletedFuture: Boolean? = null
        var paymentStatusUpdatedId: Int? = null
        var paymentStatusUpdatedToPaid: Boolean? = null

        override suspend fun getCategories(token: String) = CategoriesResponse(
            categories = listOf(Category(1, name = "Casa")),
            total = 1
        )

        override suspend fun getExpenses(
            token: String,
            month: Int,
            year: Int,
            paymentStatus: String?
        ) = ExpensesResponse(
            expenses = listOf(
                Expense(
                    id = 7,
                    category_id = 1,
                    amount = 90.0,
                    description = "Energia",
                    date = "2026-07-20T00:00:00-03:00",
                    type = "Fixa",
                    installments = null,
                    current_installment = null,
                    payment_source = "Salário"
                )
            ),
            total = 1
        )

        override suspend fun deleteExpense(
            token: String,
            id: Int,
            deleteFuture: Boolean?
        ): DefaultResponse {
            deletedId = id
            deletedFuture = deleteFuture
            return DefaultResponse("ok")
        }

        override suspend fun updateExpensePaymentStatus(
            token: String,
            id: Int,
            isPaid: Boolean
        ): ExpensePaymentStatusResponse {
            paymentStatusUpdatedId = id
            paymentStatusUpdatedToPaid = isPaid
            return ExpensePaymentStatusResponse(
                message = "Status de pagamento atualizado com sucesso!",
                expense = getExpenses(token, 7, 2026, null).expenses.single().copy(is_paid = isPaid)
            )
        }
    }
}
