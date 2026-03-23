package com.stevecampos.data.repository

import com.stevecampos.data.mock.MockBankingData
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.domain.model.Movement
import com.stevecampos.domain.repository.AccountsRepository
import com.stevecampos.domain.repository.DebugScenarioRepository
import com.stevecampos.domain.repository.SessionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockAccountsRepository @Inject constructor(
    private val debugScenarioRepository: DebugScenarioRepository,
    private val sessionRepository: SessionRepository,
) : AccountsRepository {

    override suspend fun getAccounts(): Result<List<Account>> {
        return executeListOperation(DebugOperation.GET_ACCOUNTS) {
            MockBankingData.accounts
        }
    }

    override suspend fun refreshAccounts(): Result<List<Account>> {
        return executeListOperation(DebugOperation.REFRESH_ACCOUNTS) {
            MockBankingData.accounts
        }
    }

    override suspend fun getAccount(accountId: String): Result<Account> {
        val account = MockBankingData.accounts.firstOrNull { it.id == accountId }
            ?: return Result.failure(NoSuchElementException("Cuenta no encontrada"))

        return Result.success(account)
    }

    override suspend fun getMovements(accountId: String): Result<List<Movement>> {
        delay(MOCK_DELAY_MS)
        ensureAuthorizedSession<List<Movement>>()?.let { unauthorizedResult ->
            return unauthorizedResult
        }

        val movements = MockBankingData.movementsByAccount[accountId]
            ?: return Result.failure(NoSuchElementException("Movimientos no encontrados"))

        return Result.success(movements)
    }

    private suspend fun executeListOperation(
        operation: DebugOperation,
        provider: () -> List<Account>,
    ): Result<List<Account>> {
        delay(MOCK_DELAY_MS)
        ensureAuthorizedSession<List<Account>>()?.let { unauthorizedResult ->
            return unauthorizedResult
        }

        val behavior = debugScenarioRepository.scenarios.value[operation]
        if (behavior == MockBehavior.ERROR) {
            return Result.failure(IllegalStateException("Error mock para ${operation.name}"))
        }

        return Result.success(provider())
    }

    private suspend fun <T> ensureAuthorizedSession(): Result<T>? {
        val currentSession = sessionRepository.getActiveSession()
        return if (currentSession == null || currentSession.expiresAtMillis <= System.currentTimeMillis()) {
            Result.failure(DomainException.Unauthorized)
        } else {
            null
        }
    }

    private companion object {
        const val MOCK_DELAY_MS = 3_000L
    }
}
