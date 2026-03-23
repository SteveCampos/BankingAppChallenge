package com.stevecampos.domain.usecase

import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.domain.repository.DebugScenarioRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveDebugScenariosUseCase @Inject constructor(
    private val debugScenarioRepository: DebugScenarioRepository,
) {
    operator fun invoke(): StateFlow<Map<DebugOperation, MockBehavior>> = debugScenarioRepository.scenarios
}

class UpdateDebugScenarioUseCase @Inject constructor(
    private val debugScenarioRepository: DebugScenarioRepository,
) {
    suspend operator fun invoke(
        operation: DebugOperation,
        behavior: MockBehavior,
    ) {
        debugScenarioRepository.updateBehavior(
            operation = operation,
            behavior = behavior,
        )
    }
}
