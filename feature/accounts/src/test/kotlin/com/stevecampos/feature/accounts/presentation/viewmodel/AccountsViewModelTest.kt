package com.stevecampos.feature.accounts.presentation.viewmodel

import app.cash.turbine.test
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.domain.model.SessionState
import com.stevecampos.domain.repository.AccountsRepository
import com.stevecampos.domain.repository.DebugScenarioRepository
import com.stevecampos.domain.repository.SessionRepository
import com.stevecampos.domain.usecase.GetAccountsUseCase
import com.stevecampos.domain.usecase.GetSessionStateUseCase
import com.stevecampos.domain.usecase.LogoutUseCase
import com.stevecampos.domain.usecase.ObserveDebugScenariosUseCase
import com.stevecampos.domain.usecase.RefreshAccountsUseCase
import com.stevecampos.domain.usecase.UpdateDebugScenarioUseCase
import com.stevecampos.feature.accounts.presentation.contract.AccountsContentState
import com.stevecampos.feature.accounts.presentation.contract.AccountsEffect
import com.stevecampos.feature.accounts.presentation.contract.AccountsIntent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var accountsRepository: FakeAccountsRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var debugScenarioRepository: FakeDebugScenarioRepository
    private lateinit var sut: AccountsViewModel

    @Before
    fun setup() {
        accountsRepository = FakeAccountsRepository()
        sessionRepository = FakeSessionRepository()
        debugScenarioRepository = FakeDebugScenarioRepository()
        sut = AccountsViewModel(
            getAccountsUseCase = GetAccountsUseCase(accountsRepository),
            refreshAccountsUseCase = RefreshAccountsUseCase(accountsRepository),
            getSessionStateUseCase = GetSessionStateUseCase(sessionRepository),
            logoutUseCase = LogoutUseCase(sessionRepository),
            observeDebugScenariosUseCase = ObserveDebugScenariosUseCase(debugScenarioRepository),
            updateDebugScenarioUseCase = UpdateDebugScenarioUseCase(debugScenarioRepository),
            ioDispatcher = mainDispatcherRule.dispatcher,
        )
    }

    @Test
    fun `GIVEN successful initial load WHEN screen opens THEN loading is shown and accounts are rendered`() = runTest {
        // Arrange
        accountsRepository.getAccountsResult = Result.success(sampleAccounts())
        accountsRepository.delayMillis = 1_000L

        // Act
        sut.onIntent(AccountsIntent.OnScreenOpened)
        runCurrent()

        // Assert
        assertTrue(sut.state.value.contentState is AccountsContentState.Loading)

        advanceTimeBy(1_000L)
        advanceUntilIdle()

        assertTrue(sut.state.value.contentState is AccountsContentState.Content)
        val contentState = sut.state.value.contentState as AccountsContentState.Content
        assertEquals(2, contentState.accounts.size)
        assertNull(sut.state.value.dialog)
    }

    @Test
    fun `GIVEN failed initial load WHEN screen opens THEN error item and retry dialog are shown`() = runTest {
        // Arrange
        accountsRepository.getAccountsResult = Result.failure(IllegalStateException("boom"))

        // Act
        sut.onIntent(AccountsIntent.OnScreenOpened)
        advanceUntilIdle()

        // Assert
        assertTrue(sut.state.value.contentState is AccountsContentState.Error)
        assertEquals(
            "No se pudo obtener las cuentas",
            (sut.state.value.contentState as AccountsContentState.Error).message,
        )
        assertEquals("Ha ocurrido un error, vuelve a intentarlo.", sut.state.value.dialog?.message)
        assertEquals("Reintentar", sut.state.value.dialog?.confirmText)
    }

    @Test
    fun `GIVEN existing content WHEN refresh succeeds THEN refreshing is shown and accounts are updated`() = runTest {
        // Arrange
        accountsRepository.getAccountsResult = Result.success(sampleAccounts())
        sut.onIntent(AccountsIntent.OnScreenOpened)
        advanceUntilIdle()
        accountsRepository.refreshAccountsResult = Result.success(refreshedAccounts())
        accountsRepository.delayMillis = 1_000L

        // Act
        sut.onIntent(AccountsIntent.OnRefreshRequested)
        runCurrent()

        // Assert
        assertTrue(sut.state.value.isRefreshing)

        advanceTimeBy(1_000L)
        advanceUntilIdle()

        assertFalse(sut.state.value.isRefreshing)
        val contentState = sut.state.value.contentState as AccountsContentState.Content
        assertEquals("Cuenta ahorro", contentState.accounts.first().name)
    }

    @Test
    fun `GIVEN existing content WHEN refresh fails THEN list is cleared and dismiss dialog is shown`() = runTest {
        // Arrange
        accountsRepository.getAccountsResult = Result.success(sampleAccounts())
        sut.onIntent(AccountsIntent.OnScreenOpened)
        advanceUntilIdle()
        accountsRepository.refreshAccountsResult = Result.failure(IllegalStateException("boom"))

        // Act
        sut.onIntent(AccountsIntent.OnRefreshRequested)
        advanceUntilIdle()

        // Assert
        assertFalse(sut.state.value.isRefreshing)
        assertEquals(
            "No se han podido cargar las cuentas, inténtelo de nuevo.",
            (sut.state.value.contentState as AccountsContentState.Error).message,
        )
        assertEquals("Vuelve a intentarlo", sut.state.value.dialog?.title)
        assertEquals("Aceptar", sut.state.value.dialog?.confirmText)
    }

    @Test
    fun `GIVEN unauthorized response WHEN screen opens THEN logout and navigation to login are emitted`() = runTest {
        // Arrange
        accountsRepository.getAccountsResult = Result.failure(DomainException.Unauthorized)

        // Act + Assert
        sut.effect.test {
            sut.onIntent(AccountsIntent.OnScreenOpened)
            advanceUntilIdle()

            assertEquals(AccountsEffect.Navigation.GoToLogin, awaitItem())
            assertTrue(sessionRepository.wasCleared)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN debug behavior change WHEN get accounts behavior is updated THEN state reflects the new value`() = runTest {
        // Arrange
        assertEquals(MockBehavior.SUCCESS, sut.state.value.getAccountsBehavior)

        // Act
        sut.onIntent(AccountsIntent.OnGetAccountsBehaviorChanged(MockBehavior.ERROR))
        advanceUntilIdle()

        // Assert
        assertEquals(MockBehavior.ERROR, sut.state.value.getAccountsBehavior)
    }

    private fun sampleAccounts() = listOf(
        Account(
            id = "1",
            name = "Cuenta sueldo",
            accountNumber = "001-1234567890",
            balance = 3240.50,
            currency = "S/",
        ),
        Account(
            id = "2",
            name = "Cuenta ahorro",
            accountNumber = "001-9876543210",
            balance = 980.25,
            currency = "S/",
        ),
    )

    private fun refreshedAccounts() = listOf(
        Account(
            id = "2",
            name = "Cuenta ahorro",
            accountNumber = "001-9876543210",
            balance = 1120.75,
            currency = "S/",
        ),
    )

    private class FakeAccountsRepository : AccountsRepository {
        var getAccountsResult: Result<List<Account>> = Result.success(emptyList())
        var refreshAccountsResult: Result<List<Account>> = Result.success(emptyList())
        var delayMillis: Long = 0L

        override suspend fun getAccounts(): Result<List<Account>> {
            delay(delayMillis)
            return getAccountsResult
        }

        override suspend fun refreshAccounts(): Result<List<Account>> {
            delay(delayMillis)
            return refreshAccountsResult
        }

        override suspend fun getAccount(accountId: String) =
            error("Not needed in accounts slice tests")

        override suspend fun getMovements(accountId: String) =
            error("Not needed in accounts slice tests")
    }

    private class FakeSessionRepository : SessionRepository {
        private var session = AuthSession(
            accessToken = "mock-token",
            username = "userTest1",
            issuedAtMillis = 1L,
            expiresAtMillis = 2L,
            lastActivityAtMillis = 1L,
        )
        var wasCleared: Boolean = false

        override suspend fun createSession(session: AuthSession) {
            this.session = session
        }

        override suspend fun clearSession() {
            wasCleared = true
            session = session.copy(accessToken = "")
        }

        override suspend fun touchSession() = Unit

        override suspend fun getSessionState(): SessionState = SessionState(session = session)

        override suspend fun getActiveSession(): AuthSession = session
    }

    private class FakeDebugScenarioRepository : DebugScenarioRepository {
        private val mutableScenarios = MutableStateFlow(
            DebugOperation.entries.associateWith { MockBehavior.SUCCESS },
        )

        override val scenarios: StateFlow<Map<DebugOperation, MockBehavior>> = mutableScenarios

        override suspend fun updateBehavior(
            operation: DebugOperation,
            behavior: MockBehavior,
        ) {
            mutableScenarios.value = mutableScenarios.value + (operation to behavior)
        }
    }
}
