package com.example.appfinanceiro.feature.relatorios.export

import org.junit.Assert.assertEquals
import org.junit.Test

class ReportExportRepositoryTest {
    @Test
    fun `extrai nome do content disposition`() {
        val result = extractSafeFileName(
            contentDisposition = "attachment; filename=\"relatorio-completo-2026-07.pdf\"",
            fallback = "relatorio.pdf"
        )

        assertEquals("relatorio-completo-2026-07.pdf", result)
    }

    @Test
    fun `remove caminho e caracteres invalidos do nome`() {
        val result = extractSafeFileName(
            contentDisposition = "attachment; filename=\"../pasta/relatorio:mensal?.pdf\"",
            fallback = "relatorio.pdf"
        )

        assertEquals("relatorio_mensal_.pdf", result)
    }

    @Test
    fun `cria nome alternativo com periodo e formato`() {
        val request = ReportExportRequest(
            type = ReportExportType.SUMMARY,
            month = 7,
            year = 2026,
            format = ReportExportFormat.XLSX
        )

        assertEquals("summary-2026-07.xlsx", fallbackFileName(request))
    }
}
