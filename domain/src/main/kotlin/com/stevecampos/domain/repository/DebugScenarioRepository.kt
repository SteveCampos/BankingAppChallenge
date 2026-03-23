package com.stevecampos.domain.repository

import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.MockBehavior
import kotlinx.coroutines.flow.StateFlow

interface DebugScenarioRepository {
    val scenarios: StateFlow<Map<DebugOperation, MockBehavior>>

    suspend fun updateBehavior(
        operation: DebugOperation,
        behavior: MockBehavior,
    )
}
