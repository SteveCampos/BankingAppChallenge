package com.stevecampos.data.repository

import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.model.SessionState
import com.stevecampos.domain.repository.SessionRepository
import com.stevecampos.domain.repository.TokenStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSessionRepository @Inject constructor(
    private val tokenStore: TokenStore,
) : SessionRepository {

    override suspend fun createSession(session: AuthSession) {
        tokenStore.save(session)
    }

    override suspend fun clearSession() {
        tokenStore.clear()
    }

    override suspend fun touchSession() {
        val currentSession = tokenStore.get() ?: return
        tokenStore.save(
            currentSession.copy(
                lastActivityAtMillis = currentTimeMillis(),
            ),
        )
    }

    override suspend fun getSessionState(): SessionState = SessionState(session = tokenStore.get())

    override suspend fun getActiveSession(): AuthSession? = tokenStore.get()

    private fun currentTimeMillis(): Long = System.currentTimeMillis()
}
