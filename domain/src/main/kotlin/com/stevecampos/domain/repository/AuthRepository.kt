package com.stevecampos.domain.repository

import com.stevecampos.domain.model.AuthSession

interface AuthRepository {
    suspend fun login(
        username: String,
        password: String,
    ): Result<AuthSession>
}
