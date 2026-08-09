package com.example.appfinanceiro.core.security

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayIntegrityProtectionTest {
    @Test
    fun `normalizes email with trim and lowercase using root locale`() {
        assertEquals(
            "usuario@email.com",
            PlayIntegrityProtection.normalizeEmail("  Usuario@Email.COM  ")
        )
    }

    @Test
    fun `creates the exact registration request hash`() {
        assertEquals(
            "ca05f5f534e5646863cc084c0e5e68176a8c1f58590eba464bf774867652143d",
            PlayIntegrityProtection.requestHash(
                path = "/api/auth/request-register-code",
                normalizedEmail = "usuario@email.com"
            )
        )
    }

    @Test
    fun `creates the exact password recovery request hash`() {
        assertEquals(
            "69b962e005dfb1afeac4a1930221b9213fc0d6c0bf0b9080b5dc8523d03c46a6",
            PlayIntegrityProtection.requestHash(
                path = "/api/auth/forgot-password",
                normalizedEmail = "usuario@email.com"
            )
        )
    }
}
