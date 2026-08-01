package com.example.appfinanceiro.feature.relatorios

import com.example.appfinanceiro.core.network.InstallmentCommitmentsResponse
import com.example.appfinanceiro.core.network.InstallmentCommitmentsSummary
import com.example.appfinanceiro.core.network.InstallmentPurchase
import org.junit.Assert.assertEquals
import org.junit.Test

class InstallmentCommitmentsFilterTest {
    @Test
    fun `remove compras encerradas antes do mes base`() {
        val response = responseWith(
            purchase("encerrada", endMonth = 7, endYear = 2026),
            purchase("mes-base", endMonth = 8, endYear = 2026),
            purchase("ativa", endMonth = 3, endYear = 2028)
        )

        val filtered = response.onlyCommitmentsActiveFrom(baseMonth = 8, baseYear = 2026)

        assertEquals(listOf("mes-base", "ativa"), filtered.compras.map { it.serie_id })
        assertEquals(2, filtered.resumo.total_compras)
        assertEquals(200.0, filtered.resumo.total_restante, 0.001)
    }

    @Test
    fun `permite rever compra antiga ao voltar o mes base`() {
        val response = responseWith(
            purchase("encerrada", endMonth = 7, endYear = 2026)
        )

        val filtered = response.onlyCommitmentsActiveFrom(baseMonth = 6, baseYear = 2026)

        assertEquals(1, filtered.compras.size)
    }

    private fun responseWith(vararg purchases: InstallmentPurchase) =
        InstallmentCommitmentsResponse(
            mes_base = 8,
            ano_base = 2026,
            meses = 12,
            resumo = InstallmentCommitmentsSummary(
                total_original = 0.0,
                total_pago = 0.0,
                total_restante = 0.0,
                parcelas_pagas = 0,
                parcelas_restantes = 0,
                total_compras = purchases.size,
                mes_mais_pesado = null
            ),
            compras = purchases.toList(),
            linha_do_tempo = emptyList()
        )

    private fun purchase(
        id: String,
        endMonth: Int,
        endYear: Int
    ) = InstallmentPurchase(
        serie_id = id,
        descricao = id,
        categoria_id = 1,
        categoria_nome = "Categoria",
        fonte_pagamento = "Salário",
        valor_parcela = 100.0,
        total_original = 500.0,
        total_pago = 400.0,
        total_restante = 100.0,
        parcelas_pagas = 4,
        parcelas_restantes = 1,
        total_parcelas = 5,
        primeiro_mes = 4,
        primeiro_ano = 2026,
        ultimo_mes = endMonth,
        ultimo_ano = endYear,
        proxima_parcela = null
    )
}
