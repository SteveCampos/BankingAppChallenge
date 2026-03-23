package com.stevecampos.data.repository

import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.domain.repository.DebugScenarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDebugScenarioRepository @Inject constructor() : DebugScenarioRepository {
    private val _scenarios = MutableStateFlow(
        DebugOperation.entries.associateWith { MockBehavior.SUCCESS },
    )
    override val scenarios: StateFlow<Map<DebugOperation, MockBehavior>> = _scenarios.asStateFlow()

    override suspend fun updateBehavior(
        operation: DebugOperation,
        behavior: MockBehavior,
    ) {
        _scenarios.update { current ->
            current + (operation to behavior)
        }
    }
}
