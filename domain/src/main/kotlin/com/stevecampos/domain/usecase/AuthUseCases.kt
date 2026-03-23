package com.stevecampos.domain.usecase

import com.stevecampos.domain.repository.AuthRepository
import com.stevecampos.domain.repository.SessionRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): Result<Unit> {
        val loginResult = authRepository.login(
            username = username,
            password = password,
        )

        return loginResult.mapCatching { authenticatedSession ->
            sessionRepository.createSession(authenticatedSession)
        }
    }
}
