package com.stevecampos.domain.repository

import com.stevecampos.domain.model.AuthSession

interface TokenStore {
    suspend fun save(session: AuthSession)

    suspend fun get(): AuthSession?

    suspend fun clear()
}
