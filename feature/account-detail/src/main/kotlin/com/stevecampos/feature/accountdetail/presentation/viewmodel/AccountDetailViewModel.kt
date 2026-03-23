package com.stevecampos.feature.accountdetail.presentation.viewmodel

import com.stevecampos.core.ui.mvi.MviViewModel
import com.stevecampos.core.ui.util.formatCurrency
import com.stevecampos.domain.coroutines.IoDispatcher
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.usecase.GetAccountMovementsUseCase
import com.stevecampos.domain.usecase.GetAccountUseCase
import com.stevecampos.domain.usecase.LogoutUseCase
import com.stevecampos.feature.accountdetail.R
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailContentState
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailEffect
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailIntent
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val getAccountMovementsUseCase: GetAccountMovementsUseCase,
    private val logoutUseCase: LogoutUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MviViewModel<AccountDetailState, AccountDetailIntent, AccountDetailEffect>(
    initialState = AccountDetailState(),
    defaultDispatcher = ioDispatcher,
) {

    override fun onIntent(intent: AccountDetailIntent) {
        when (intent) {
            is AccountDetailIntent.OnScreenOpened -> loadAccountDetail(intent.accountId)
            AccountDetailIntent.OnRetryClicked -> state.value.accountId?.let(::loadAccountDetail)
            AccountDetailIntent.OnCopyAccountNumberClicked -> emitCopyAccountNumber()
            AccountDetailIntent.OnShareAccountDetailsClicked -> emitShareDetails()
        }
    }

    private fun loadAccountDetail(accountId: String) {
        updateState {
            copy(
                accountId = accountId,
                contentState = AccountDetailContentState.Loading,
            )
        }

        executeUseCase(
            onSuccess = { contentState ->
                updateState { copy(contentState = contentState) }
            },
            onError = { throwable ->
                handleLoadError(throwable)
            },
        ) {
            getAccountUseCase(accountId).fold(
                onSuccess = { account ->
                    getAccountMovementsUseCase(accountId).map { movements ->
                        AccountDetailContentState.Content(
                            account = account,
                            movements = movements,
                        )
                    }
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                },
            )
        }
    }

    private suspend fun handleLoadError(throwable: Throwable) {
        if (throwable == DomainException.Unauthorized) {
            logoutUseCase()
            emitEffect(AccountDetailEffect.Navigation.GoToLogin)
            return
        }

        updateState {
            copy(
                contentState = AccountDetailContentState.Error(
                    R.string.account_detail_error_movements,
                ),
            )
        }
    }

    private fun emitCopyAccountNumber() {
        val contentState = state.value.contentState as? AccountDetailContentState.Content ?: return
        emitEffect(AccountDetailEffect.CopyAccountNumber(contentState.account.accountNumber))
    }

    private fun emitShareDetails() {
        val contentState = state.value.contentState as? AccountDetailContentState.Content ?: return
        val account = contentState.account
        emitEffect(
            AccountDetailEffect.ShareAccountDetails(
                accountName = account.name,
                accountNumber = account.accountNumber,
                formattedBalance = formatCurrency(account.balance, account.currency),
            ),
        )
    }
}
