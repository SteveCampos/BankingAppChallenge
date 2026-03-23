package com.stevecampos.feature.login.presentation.viewmodel

import app.cash.turbine.test
import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.domain.model.SessionState
import com.stevecampos.domain.repository.AuthRepository
import com.stevecampos.domain.repository.DebugScenarioRepository
import com.stevecampos.domain.repository.SessionRepository
import com.stevecampos.domain.usecase.LoginUseCase
import com.stevecampos.domain.usecase.ObserveDebugScenariosUseCase
import com.stevecampos.domain.usecase.UpdateDebugScenarioUseCase
import com.stevecampos.feature.login.presentation.contract.LoginEffect
import com.stevecampos.feature.login.presentation.contract.LoginIntent
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var debugScenarioRepository: FakeDebugScenarioRepository
    private lateinit var sut: LoginViewModel

    @Before
    fun setup() {
        authRepository = FakeAuthRepository()
        sessionRepository = FakeSessionRepository()
        debugScenarioRepository = FakeDebugScenarioRepository()
        sut = LoginViewModel(
            loginUseCase = LoginUseCase(authRepository, sessionRepository),
            observeDebugScenariosUseCase = ObserveDebugScenariosUseCase(debugScenarioRepository),
            updateDebugScenarioUseCase = UpdateDebugScenarioUseCase(debugScenarioRepository),
            ioDispatcher = mainDispatcherRule.dispatcher,
        )
    }

    @Test
    fun `GIVEN username with emoji WHEN username changes THEN emoji is removed`() = runTest {
        // Arrange
        val usernameWithEmoji = "user😀Test1"

        // Act
        sut.onIntent(LoginIntent.OnUsernameChanged(usernameWithEmoji))

        // Assert
        assertEquals("userTest1", sut.state.value.username)
    }

    @Test
    fun `GIVEN password with emoji WHEN password changes THEN emoji is removed`() = runTest {
        // Arrange
        val passwordWithEmoji = "pass🔒Test1"

        // Act
        sut.onIntent(LoginIntent.OnPasswordChanged(passwordWithEmoji))

        // Assert
        assertEquals("passTest1", sut.state.value.password)
    }

    @Test
    fun `GIVEN hidden password WHEN visibility is toggled THEN password becomes visible`() = runTest {
        // Arrange
        assertFalse(sut.state.value.isPasswordVisible)

        // Act
        sut.onIntent(LoginIntent.OnPasswordVisibilityClicked)

        // Assert
        assertTrue(sut.state.value.isPasswordVisible)
    }

    @Test
    fun `GIVEN valid credentials WHEN login is requested THEN loading is shown and navigation is emitted`() = runTest {
        // Arrange
        authRepository.loginResult = Result.success(createSession("userTest1"))
        authRepository.responseDelayMillis = 1_000L
        sut.onIntent(LoginIntent.OnUsernameChanged("userTest1"))
        sut.onIntent(LoginIntent.OnPasswordChanged("passTest1"))

        // Act + Assert
        sut.effect.test {
            sut.onIntent(LoginIntent.OnLoginClicked)
            runCurrent()
            assertTrue(sut.state.value.isLoading)

            advanceTimeBy(1_000L)
            advanceUntilIdle()

            assertEquals(LoginEffect.Navigation.GoToHome, awaitItem())
            assertFalse(sut.state.value.isLoading)
            assertEquals("userTest1", sessionRepository.savedSession?.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN invalid credentials WHEN login is requested THEN loading is hidden and error is shown`() = runTest {
        // Arrange
        authRepository.loginResult = Result.failure(DomainException.InvalidCredentials)
        authRepository.responseDelayMillis = 1_000L
        sut.onIntent(LoginIntent.OnUsernameChanged("userTest1"))
        sut.onIntent(LoginIntent.OnPasswordChanged("wrongPass"))

        // Act + Assert
        sut.effect.test {
            sut.onIntent(LoginIntent.OnLoginClicked)
            runCurrent()
            assertTrue(sut.state.value.isLoading)

            advanceTimeBy(1_000L)
            advanceUntilIdle()

            assertFalse(sut.state.value.isLoading)
            assertEquals("Usuario y/o contraseña incorrectos", sut.state.value.errorMessage)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createSession(username: String) = AuthSession(
        accessToken = "mock-token",
        username = username,
        issuedAtMillis = 1L,
        expiresAtMillis = 2L,
        lastActivityAtMillis = 1L,
    )

    private class FakeAuthRepository : AuthRepository {
        var loginResult: Result<AuthSession> = Result.success(
            AuthSession(
                accessToken = "token",
                username = "userTest1",
                issuedAtMillis = 1L,
                expiresAtMillis = 2L,
                lastActivityAtMillis = 1L,
            ),
        )
        var responseDelayMillis: Long = 0L

        override suspend fun login(
            username: String,
            password: String,
        ): Result<AuthSession> {
            delay(responseDelayMillis)
            return loginResult
        }
    }

    private class FakeSessionRepository : SessionRepository {
        var savedSession: AuthSession? = null

        override suspend fun createSession(session: AuthSession) {
            savedSession = session
        }

        override suspend fun clearSession() {
            savedSession = null
        }

        override suspend fun touchSession() = Unit

        override suspend fun getSessionState(): SessionState = SessionState(session = savedSession)

        override suspend fun getActiveSession(): AuthSession? = savedSession
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
