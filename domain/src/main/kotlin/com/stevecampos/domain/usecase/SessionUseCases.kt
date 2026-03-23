package com.stevecampos.domain.usecase

import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.model.SessionState
import com.stevecampos.domain.repository.SessionRepository
import javax.inject.Inject

class GetSessionStateUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(): SessionState = sessionRepository.getSessionState()
}

class GetActiveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(): AuthSession? = sessionRepository.getActiveSession()
}

class TouchSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke() = sessionRepository.touchSession()
}

class LogoutUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke() = sessionRepository.clearSession()
}
