package com.stevecampos.feature.accountdetail.presentation.viewmodel

import app.cash.turbine.test
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.model.Movement
import com.stevecampos.domain.model.MovementType
import com.stevecampos.domain.model.SessionState
import com.stevecampos.domain.repository.AccountsRepository
import com.stevecampos.domain.repository.SessionRepository
import com.stevecampos.domain.usecase.GetAccountMovementsUseCase
import com.stevecampos.domain.usecase.GetAccountUseCase
import com.stevecampos.domain.usecase.LogoutUseCase
import com.stevecampos.feature.accountdetail.R
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailContentState
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailEffect
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailIntent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var accountsRepository: FakeAccountsRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var sut: AccountDetailViewModel

    @Before
    fun setup() {
        accountsRepository = FakeAccountsRepository()
        sessionRepository = FakeSessionRepository()
        sut = AccountDetailViewModel(
            getAccountUseCase = GetAccountUseCase(accountsRepository),
            getAccountMovementsUseCase = GetAccountMovementsUseCase(accountsRepository),
            logoutUseCase = LogoutUseCase(sessionRepository),
            ioDispatcher = mainDispatcherRule.dispatcher,
        )
    }

    @Test
    fun `GIVEN valid account detail WHEN screen opens THEN loading is shown and content is rendered`() = runTest {
        // Arrange
        accountsRepository.getAccountResult = Result.success(sampleAccount())
        accountsRepository.getMovementsResult = Result.success(sampleMovements())
        accountsRepository.delayMillis = 1_000L

        // Act
        sut.onIntent(AccountDetailIntent.OnScreenOpened("001"))
        runCurrent()

        // Assert
        assertTrue(sut.state.value.contentState is AccountDetailContentState.Loading)

        advanceTimeBy(1_000L)
        advanceUntilIdle()

        val contentState = sut.state.value.contentState as AccountDetailContentState.Content
        assertEquals("Cuenta Sueldo", contentState.account.name)
        assertEquals(2, contentState.movements.size)
    }

    @Test
    fun `GIVEN movements error WHEN screen opens THEN error state is shown`() = runTest {
        // Arrange
        accountsRepository.getAccountResult = Result.success(sampleAccount())
        accountsRepository.getMovementsResult = Result.failure(IllegalStateException("No se pudieron obtener los movimientos"))

        // Act
        sut.onIntent(AccountDetailIntent.OnScreenOpened("001"))
        advanceUntilIdle()

        // Assert
        val contentState = sut.state.value.contentState as AccountDetailContentState.Error
        assertEquals(R.string.account_detail_error_movements, contentState.messageRes)
    }

    @Test
    fun `GIVEN unauthorized movements WHEN screen opens THEN logout and login navigation are emitted`() = runTest {
        // Arrange
        accountsRepository.getAccountResult = Result.success(sampleAccount())
        accountsRepository.getMovementsResult = Result.failure(DomainException.Unauthorized)

        // Act + Assert
        sut.effect.test {
            sut.onIntent(AccountDetailIntent.OnScreenOpened("001"))
            advanceUntilIdle()

            assertEquals(AccountDetailEffect.Navigation.GoToLogin, awaitItem())
            assertTrue(sessionRepository.wasCleared)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN loaded content WHEN copy account is tapped THEN copy effect is emitted`() = runTest {
        // Arrange
        accountsRepository.getAccountResult = Result.success(sampleAccount())
        accountsRepository.getMovementsResult = Result.success(sampleMovements())
        sut.onIntent(AccountDetailIntent.OnScreenOpened("001"))
        advanceUntilIdle()

        // Act + Assert
        sut.effect.test {
            sut.onIntent(AccountDetailIntent.OnCopyAccountNumberClicked)
            assertEquals(
                AccountDetailEffect.CopyAccountNumber("001-12345678-90"),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN loaded content WHEN share detail is tapped THEN share effect is emitted`() = runTest {
        // Arrange
        accountsRepository.getAccountResult = Result.success(sampleAccount())
        accountsRepository.getMovementsResult = Result.success(sampleMovements())
        sut.onIntent(AccountDetailIntent.OnScreenOpened("001"))
        advanceUntilIdle()

        // Act + Assert
        sut.effect.test {
            sut.onIntent(AccountDetailIntent.OnShareAccountDetailsClicked)
            val effect = awaitItem() as AccountDetailEffect.ShareAccountDetails
            assertEquals("Cuenta Sueldo", effect.accountName)
            assertEquals("001-12345678-90", effect.accountNumber)
            assertTrue(effect.formattedBalance.startsWith("S/"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun sampleAccount() = Account(
        id = "001",
        name = "Cuenta Sueldo",
        accountNumber = "001-12345678-90",
        balance = 15890.45,
        currency = "S/",
    )

    private fun sampleMovements() = listOf(
        Movement(
            id = "m001",
            title = "Transferencia",
            description = "",
            amount = 6.10,
            date = "Hoy",
            type = MovementType.CREDIT,
        ),
        Movement(
            id = "m002",
            title = "Plin",
            description = "",
            amount = 10.00,
            date = "13 Mar 2026",
            type = MovementType.DEBIT,
        ),
    )

    private class FakeAccountsRepository : AccountsRepository {
        var getAccountResult: Result<Account> = Result.failure(IllegalStateException("Missing account"))
        var getMovementsResult: Result<List<Movement>> = Result.success(emptyList())
        var delayMillis: Long = 0L

        override suspend fun getAccounts() =
            error("Not needed in account detail tests")

        override suspend fun refreshAccounts() =
            error("Not needed in account detail tests")

        override suspend fun getAccount(accountId: String): Result<Account> = getAccountResult

        override suspend fun getMovements(accountId: String): Result<List<Movement>> {
            delay(delayMillis)
            return getMovementsResult
        }
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
}
