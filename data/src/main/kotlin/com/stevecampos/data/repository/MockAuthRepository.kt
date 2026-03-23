package com.stevecampos.data.repository

import com.stevecampos.data.mock.MockBankingData
import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.domain.repository.AuthRepository
import com.stevecampos.domain.repository.DebugScenarioRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockAuthRepository @Inject constructor(
    private val debugScenarioRepository: DebugScenarioRepository,
) : AuthRepository {

    override suspend fun login(
        username: String,
        password: String,
    ): Result<AuthSession> {
        delay(MOCK_DELAY_MS)
        val expectedPassword = MockBankingData.validUsers[username]
        return if (expectedPassword == password) {
            Result.success(
                createSession(username),
            )
        } else {
            Result.failure(DomainException.InvalidCredentials)
        }
    }

    private fun createSession(username: String): AuthSession {
        val now = System.currentTimeMillis()
        return AuthSession(
            accessToken = "mock-token-${username.hashCode()}-$now",
            username = username,
            issuedAtMillis = now,
            expiresAtMillis = now + SESSION_DURATION_MS,
            lastActivityAtMillis = now,
        )
    }

    private companion object {
        const val MOCK_DELAY_MS = 3_000L
        const val SESSION_DURATION_MS = 2 * 60 * 1000L
    }
}
