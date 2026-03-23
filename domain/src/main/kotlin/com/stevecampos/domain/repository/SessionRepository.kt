package com.stevecampos.domain.repository

import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.model.SessionState

interface SessionRepository {
    suspend fun createSession(session: AuthSession)

    suspend fun clearSession()

    suspend fun touchSession()

    suspend fun getSessionState(): SessionState

    suspend fun getActiveSession(): AuthSession?
}
