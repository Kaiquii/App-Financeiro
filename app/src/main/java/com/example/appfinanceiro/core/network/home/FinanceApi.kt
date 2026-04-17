package com.example.appfinanceiro.core.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class SummaryResponse(
    val month: Int,
    val year: Int,
    val renda_extra_amt: Double,
    val restante_adiantamento: Double,
    val restante_salario: Double,
    val total_expense: Double,
    val total_geral_disponivel: Double,
    val total_income: Double
)

data class Category(val id: Int, val name: String)
data class CategoriesResponse(val categories: List<Category>, val total: Int)

data class Expense(
    val id: Int,
    val category_id: Int,
    val amount: Double,
    val description: String,
    val date: String,
    val type: String,
    val installments: Int?,
    val current_installment: Int?,
    val payment_source: String?
)
data class ExpensesResponse(val expenses: List<Expense>, val total: Int)

data class Income(
    val id: Int,
    val source: String,
    val amount: Double,
    val month: Int,
    val year: Int
)
data class IncomesResponse(val incomes: List<Income>, val total: Int)

interface FinanceApi {
    @GET("api/reports/summary")
    suspend fun getSummary(
        @Header("Authorization") token: String,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): SummaryResponse

    @GET("api/categories/")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): CategoriesResponse

    @GET("api/expenses")
    suspend fun getExpenses(
        @Header("Authorization") token: String,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): ExpensesResponse

    @GET("api/incomes")
    suspend fun getIncomes(
        @Header("Authorization") token: String,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): IncomesResponse
}