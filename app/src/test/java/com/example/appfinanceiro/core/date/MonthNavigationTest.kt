package com.example.appfinanceiro.core.date

import org.junit.Assert.assertEquals
import org.junit.Test

class MonthNavigationTest {

    @Test
    fun `volta de setembro para agosto independentemente do dia atual`() {
        assertEquals(MonthYear(monthIndex = 7, year = 2026), shiftMonth(8, 2026, -1))
    }

    @Test
    fun `volta de novembro para outubro`() {
        assertEquals(MonthYear(monthIndex = 9, year = 2026), shiftMonth(10, 2026, -1))
    }

    @Test
    fun `atravessa a virada do ano nas duas direcoes`() {
        assertEquals(MonthYear(monthIndex = 0, year = 2027), shiftMonth(11, 2026, 1))
        assertEquals(MonthYear(monthIndex = 11, year = 2025), shiftMonth(0, 2026, -1))
    }
}
