package com.stevecampos.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class MviViewModel<S : Any, I : Any, E : Any>(
    initialState: S,
    private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<E>(replay = 0, extraBufferCapacity = 1)
    val effect: SharedFlow<E> = _effect.asSharedFlow()

    abstract fun onIntent(intent: I)

    protected fun updateState(reducer: S.() -> S) {
        _state.update { currentState ->
            currentState.reducer()
        }
    }

    protected fun emitEffect(effect: E) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    protected fun <T> executeUseCase(
        dispatcher: CoroutineDispatcher = defaultDispatcher,
        onSuccess: suspend (T) -> Unit = {},
        onError: suspend (Throwable) -> Unit = {},
        block: suspend () -> Result<T>,
    ) {
        viewModelScope.launch {
            val result = try {
                withContext(dispatcher) { block() }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (throwable: Throwable) {
                Result.failure(throwable)
            }

            result
                .onSuccess { value ->
                    onSuccess(value)
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) {
                        throw throwable
                    }
                    onError(throwable)
                }
        }
    }
}
