package com.stevecampos.feature.accounts.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.stevecampos.core.ui.mvi.MviViewModel
import com.stevecampos.domain.coroutines.IoDispatcher
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.DebugOperation
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.domain.usecase.GetAccountsUseCase
import com.stevecampos.domain.usecase.GetSessionStateUseCase
import com.stevecampos.domain.usecase.LogoutUseCase
import com.stevecampos.domain.usecase.ObserveDebugScenariosUseCase
import com.stevecampos.domain.usecase.RefreshAccountsUseCase
import com.stevecampos.domain.usecase.UpdateDebugScenarioUseCase
import com.stevecampos.feature.accounts.presentation.contract.AccountsDialogAction
import com.stevecampos.feature.accounts.presentation.contract.AccountsContentState
import com.stevecampos.feature.accounts.presentation.contract.AccountsDialogState
import com.stevecampos.feature.accounts.presentation.contract.AccountsEffect
import com.stevecampos.feature.accounts.presentation.contract.AccountsIntent
import com.stevecampos.feature.accounts.presentation.contract.AccountsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val refreshAccountsUseCase: RefreshAccountsUseCase,
    private val getSessionStateUseCase: GetSessionStateUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val observeDebugScenariosUseCase: ObserveDebugScenariosUseCase,
    private val updateDebugScenarioUseCase: UpdateDebugScenarioUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MviViewModel<AccountsState, AccountsIntent, AccountsEffect>(
    initialState = AccountsState(),
    defaultDispatcher = ioDispatcher,
) {

    init {
        observeDebugScenarios()
        loadSessionData()
    }

    override fun onIntent(intent: AccountsIntent) {
        when (intent) {
            AccountsIntent.OnDialogConfirmClicked -> handleDialogConfirm()
            AccountsIntent.OnDialogDismissed -> dismissDialog()
            AccountsIntent.OnRefreshRequested -> refreshAccounts()
            AccountsIntent.OnRetryInitialLoadClicked -> loadAccounts(force = true)
            AccountsIntent.OnScreenOpened -> loadAccounts(force = false)
            is AccountsIntent.OnGetAccountsBehaviorChanged -> {
                updateDebugScenario(
                    operation = DebugOperation.GET_ACCOUNTS,
                    behavior = intent.behavior,
                )
            }

            is AccountsIntent.OnRefreshAccountsBehaviorChanged -> {
                updateDebugScenario(
                    operation = DebugOperation.REFRESH_ACCOUNTS,
                    behavior = intent.behavior,
                )
            }
        }
    }

    private fun observeDebugScenarios() {
        viewModelScope.launch {
            observeDebugScenariosUseCase().collect { scenarios ->
                updateState {
                    copy(
                        getAccountsBehavior = scenarios[DebugOperation.GET_ACCOUNTS]
                            ?: MockBehavior.SUCCESS,
                        refreshAccountsBehavior = scenarios[DebugOperation.REFRESH_ACCOUNTS]
                            ?: MockBehavior.SUCCESS,
                        getMovementsBehavior = scenarios[DebugOperation.GET_MOVEMENTS]
                            ?: MockBehavior.SUCCESS,
                    )
                }
            }
        }
    }

    private fun loadSessionData() {
        viewModelScope.launch {
            val sessionState = getSessionStateUseCase()
            updateState { copy(userName = sessionState.session?.username) }
        }
    }

    private fun loadAccounts(force: Boolean) {
        val currentState = state.value
        if (!force && currentState.contentState !is AccountsContentState.Empty) {
            return
        }

        updateState {
            copy(
                isRefreshing = false,
                contentState = AccountsContentState.Loading,
                dialog = null,
            )
        }

        executeUseCase(
            onSuccess = { accounts ->
                updateState {
                    copy(
                        contentState = AccountsContentState.Content(accounts),
                        dialog = null,
                    )
                }
            },
            onError = { throwable ->
                handleInitialLoadError(throwable)
            },
        ) {
            getAccountsUseCase()
        }
    }

    private fun refreshAccounts() {
        val currentState = state.value
        if (
            currentState.contentState is AccountsContentState.Loading ||
            currentState.isRefreshing
        ) {
            return
        }

        updateState {
            copy(
                isRefreshing = true,
                dialog = null,
            )
        }

        executeUseCase(
            onSuccess = { accounts ->
                updateState {
                    copy(
                        isRefreshing = false,
                        contentState = AccountsContentState.Content(accounts),
                        dialog = null,
                    )
                }
            },
            onError = { throwable ->
                handleRefreshError(throwable)
            },
        ) {
            refreshAccountsUseCase()
        }
    }

    private suspend fun handleInitialLoadError(throwable: Throwable) {
        if (throwable == DomainException.Unauthorized) {
            handleUnauthorized()
            return
        }

        updateState {
            copy(
                isRefreshing = false,
                contentState = AccountsContentState.Error(INITIAL_LOAD_ITEM_MESSAGE),
                dialog = AccountsDialogState(
                    title = DEFAULT_DIALOG_TITLE,
                    message = INITIAL_LOAD_DIALOG_MESSAGE,
                    confirmText = INITIAL_LOAD_CONFIRM_TEXT,
                    action = AccountsDialogAction.RETRY_INITIAL_LOAD,
                ),
            )
        }
    }

    private suspend fun handleRefreshError(throwable: Throwable) {
        if (throwable == DomainException.Unauthorized) {
            handleUnauthorized()
            return
        }

        updateState {
            copy(
                isRefreshing = false,
                contentState = AccountsContentState.Error(REFRESH_ITEM_MESSAGE),
                dialog = AccountsDialogState(
                    title = REFRESH_DIALOG_TITLE,
                    message = REFRESH_ITEM_MESSAGE,
                    confirmText = REFRESH_CONFIRM_TEXT,
                    action = AccountsDialogAction.DISMISS,
                ),
            )
        }
    }

    private suspend fun handleUnauthorized() {
        updateState {
            copy(
                isRefreshing = false,
                dialog = null,
                contentState = AccountsContentState.Empty,
            )
        }
        logoutUseCase()
        emitEffect(AccountsEffect.Navigation.GoToLogin)
    }

    private fun dismissDialog() {
        updateState { copy(dialog = null) }
    }

    private fun handleDialogConfirm() {
        when (state.value.dialog?.action) {
            AccountsDialogAction.RETRY_INITIAL_LOAD -> {
                dismissDialog()
                loadAccounts(force = true)
            }

            AccountsDialogAction.DISMISS -> dismissDialog()
            null -> Unit
        }
    }

    private fun updateDebugScenario(
        operation: DebugOperation,
        behavior: MockBehavior,
    ) {
        viewModelScope.launch {
            updateDebugScenarioUseCase(
                operation = operation,
                behavior = behavior,
            )
        }
    }

    private companion object {
        const val DEFAULT_DIALOG_TITLE = "Error"
        const val INITIAL_LOAD_ITEM_MESSAGE = "No se pudo obtener las cuentas"
        const val INITIAL_LOAD_DIALOG_MESSAGE = "Ha ocurrido un error, vuelve a intentarlo."
        const val INITIAL_LOAD_CONFIRM_TEXT = "Reintentar"
        const val REFRESH_DIALOG_TITLE = "Vuelve a intentarlo"
        const val REFRESH_ITEM_MESSAGE = "No se han podido cargar las cuentas, inténtelo de nuevo."
        const val REFRESH_CONFIRM_TEXT = "Aceptar"
    }
}
